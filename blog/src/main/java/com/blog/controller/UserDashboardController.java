package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.entity.*;
import com.blog.service.ArticleService;
import com.blog.service.CategoryService;
import com.blog.service.CommentService;
import com.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // 导入重定向消息类

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest; 

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserDashboardController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final UserService userService;

    /* ================ 文章管理 ================ */

    @GetMapping("/article")
    public String articleList(Model model, HttpSession session,
                              @RequestParam(required = false) String title) {
        User user = (User) session.getAttribute("loginUser");

        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getAuthorId, user.getId())
                .like(title != null && !title.isEmpty(), Article::getTitle, title);

        model.addAttribute("list", articleService.list(queryWrapper));
        model.addAttribute("title", title);
        return "user/article-list";
    }

    @GetMapping("/article/publish")
    public String articleForm(@RequestParam(required = false) Long id, Model model) {
        if (id != null) {
            model.addAttribute("a", articleService.getById(id));
        }
        model.addAttribute("categories", categoryService.list());
        return "user/article-form";
    }

    @PostMapping("/article")
    public String save(Article a, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        a.setAuthorId(user.getId());
        a.setStatus(0);
        articleService.saveOrUpdate(a);
        return "redirect:/user/article";
    }

    /**
     * 修改删除文章的方法：调用服务层的自定义删除方法，添加异常处理和提示
     */
    @GetMapping("/article/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // 调用服务层自定义的删除方法（先删评论再删文章）
            // 如果服务层重写了removeById，也可以用articleService.removeById(id);
            boolean isSuccess = articleService.removeArticleWithComment(id);
            if (isSuccess) {
                redirectAttributes.addFlashAttribute("message", "文章删除成功！");
            } else {
                redirectAttributes.addFlashAttribute("error", "文章删除失败，文章不存在！");
            }
        } catch (Exception e) {
            // 捕获异常，给用户提示
            redirectAttributes.addFlashAttribute("error", "删除失败：" + e.getMessage());
            e.printStackTrace(); // 开发环境打印异常，生产环境可移除
        }
        return "redirect:/user/article";
    }

    /* ================ 文章详情查看（包含评论） ================ */

    @GetMapping("/article/view/{id}")
    public String viewArticle(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");

        Article article = articleService.getById(id);
        if (article == null) {
            return "redirect:/user/article";
        }

        // 增加点击量
        articleService.incrViewNum(id);

        // 获取该文章的评论
        List<Comment> comments = commentService.lambdaQuery()
                .eq(Comment::getArticleId, id)
                .orderByDesc(Comment::getCreateTime)
                .list();

        // 获取用户映射
        Map<Long, String> userMap = comments.stream()
                .map(Comment::getUserId)
                .distinct()
                .map(uid -> {
                    User u = userService.getById(uid);
                    if (u == null) {
                        u = new User();
                        u.setId(uid);
                        u.setUsername("用户" + uid);
                    }
                    return u;
                })
                .collect(Collectors.toMap(User::getId, User::getUsername));

        model.addAttribute("a", article);
        model.addAttribute("comments", comments);
        model.addAttribute("userMap", userMap);
        model.addAttribute("loginUser", user);

        return "user/article-view";
    }

    /* ================ 评论管理 ================ */

    @GetMapping("/comments")
    public String commentList(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");

        List<Comment> comments = commentService.lambdaQuery()
                .eq(Comment::getUserId, user.getId())
                .orderByDesc(Comment::getCreateTime)
                .list();

        // 修复：获取文章标题映射
        Map<Long, String> articleMap = comments.stream()
                .map(Comment::getArticleId)
                .distinct()
                .collect(Collectors.toMap(
                        articleId -> articleId,
                        articleId -> {
                            Article article = articleService.getById(articleId);
                            return article != null ? article.getTitle() : "文章已删除";
                        }
                ));

        model.addAttribute("comments", comments);
        model.addAttribute("articleMap", articleMap);

        return "user/comments";
    }

    @PostMapping("/comment/submit")
    public String submitComment(@RequestParam Long articleId,
                                @RequestParam String content,
                                HttpSession session) {
        User user = (User) session.getAttribute("loginUser");

        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(user.getId());
        comment.setContent(content);
        comment.setCreateTime(LocalDateTime.now());

        commentService.save(comment);

        return "redirect:/article/" + articleId;
    }

    @GetMapping("/comments/delete/{id}")
    public String deleteComment(@PathVariable Long id,
                                HttpSession session,
                                HttpServletRequest request) {
        User user = (User) session.getAttribute("loginUser");
        Comment comment = commentService.getById(id);

        if (comment != null && comment.getUserId().equals(user.getId())) {
            commentService.removeById(id);
        }

        // 获取来源页面，判断是从文章详情页还是评论列表页
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/article/")) {
            // 从文章详情页删除，返回文章详情页
            return "redirect:" + referer;
        }

        // 从评论列表页删除，返回评论列表页
        return "redirect:/user/comments";
    }
}