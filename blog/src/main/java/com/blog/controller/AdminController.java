package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.entity.*;
import com.blog.service.ArticleService;
import com.blog.service.CategoryService;
import com.blog.service.UserService;  // 1. 导入UserService
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final UserService userService;  // 2. 添加UserService成员变量

    @GetMapping
    public String adminIndex() {
        return "admin/index"; // 指向新创建的管理员首页
    }

    @GetMapping("/category")
    public String categoryList(Model model, @RequestParam(required = false) String name) {
        // 构建查询条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            queryWrapper.like(Category::getName, name);
        }
        // 执行查询
        model.addAttribute("list", categoryService.list(queryWrapper));
        // 回显查询条件
        model.addAttribute("name", name);
        return "admin/category";
    }

    @GetMapping("/category/delete/{id}")
    public String delCategory(@PathVariable Long id) {
        // 关键：检查该分类下是否有关联文章
        boolean hasArticles = articleService.lambdaQuery()
                .eq(Article::getCategoryId, id) // 关联分类ID
                .exists(); // 判断是否存在文章

        if (hasArticles) {
            // 有文章关联，跳转并携带错误参数（与前端提示对应）
            return "redirect:/admin/category?error=has_articles";
        }

        // 无关联文章，执行删除
        categoryService.removeById(id);
        return "redirect:/admin/category";
    }

    @GetMapping("/audit")
    public String auditList(Model model) {
        // 查询待审核文章
        List<Article> articles = articleService.lambdaQuery()
                .eq(Article::getStatus, 0)
                .orderByDesc(Article::getCreateTime)
                .list();

        // 批量获取作者名称
        Map<Long, String> authorMap = new HashMap<>();
        if (!articles.isEmpty()) {
            List<Long> authorIds = articles.stream()
                    .map(Article::getAuthorId)
                    .distinct()
                    .collect(Collectors.toList());

            authorMap = userService.listByIds(authorIds).stream()
                    .collect(Collectors.toMap(User::getId, User::getUsername));
        }

        model.addAttribute("list", articles);
        model.addAttribute("authorMap", authorMap);  // 传递作者映射到前端
        return "admin/audit";
    }

    @PostMapping("/audit")
    @ResponseBody
    public R audit(@RequestParam Long id, @RequestParam Integer status) {
        Article a = new Article();
        a.setId(id);
        a.setStatus(status);
        articleService.updateById(a);
        return R.ok();
    }

    @GetMapping("/category/edit/{id}")
    public String editCategory(@PathVariable Long id, Model model) {
        model.addAttribute("c", categoryService.getById(id));
        return "admin/category-form";
    }

    /* 新增/修改统一保存 */
    @PostMapping("/category")
    public String saveCategory(Category c) {
        categoryService.saveOrUpdate(c);   // MyBatis-Plus 自带
        return "redirect:/admin/category";
    }
}