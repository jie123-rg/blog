package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.entity.Comment;
import com.blog.entity.User;
import com.blog.service.ArticleService;
import com.blog.service.CategoryService;
import com.blog.service.CommentService;
import com.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession; // 添加这行导入
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final UserService userService;

    @GetMapping("/")
    public String index(@RequestParam(defaultValue = "1") Integer current,
                        @RequestParam(required = false) Long categoryId,
                        Model model) {
        IPage<Article> page = articleService.pageFront(current, categoryId);
        List<Category> categories = categoryService.list();
        Map<Long, String> cateMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
        model.addAttribute("page", page);
        model.addAttribute("categories", categories);
        model.addAttribute("cateMap", cateMap);
        return "index";
    }

    @GetMapping("/article/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) { // 添加 HttpSession 参数
        Article a = articleService.getById(id);
        if (a == null || a.getStatus() != 1) throw new RuntimeException("文章不存在");
        articleService.incrViewNum(id);
        model.addAttribute("a", a);

        // 获取分类映射
        Map<Long, String> cateMap = categoryService.list().stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
        model.addAttribute("cateMap", cateMap);

        List<Comment> comments = commentService.lambdaQuery()
                .eq(Comment::getArticleId, id)
                .orderByDesc(Comment::getCreateTime)
                .list();

        // 获取用户映射
        Map<Long, String> userMap = comments.stream()
                .map(Comment::getUserId)
                .distinct()
                .map(userService::getById)
                .collect(Collectors.toMap(User::getId, User::getUsername));

        model.addAttribute("comments", comments);
        model.addAttribute("userMap", userMap);

        return "detail";
    }

    @GetMapping("/home")
    public String home(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(required = false) Long categoryId,
                       Model model) {
        IPage<Article> page = articleService.pageFront(current, categoryId);
        List<Category> categories = categoryService.list();
        Map<Long, String> cateMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
        model.addAttribute("page", page);
        model.addAttribute("categories", categories);
        model.addAttribute("cateMap", cateMap);
        return "index";
    }
}