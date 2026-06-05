package com.blog.controller;

import com.blog.entity.User;
import com.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam Integer role,
                        HttpSession session) {

        User u = userService.lambdaQuery().eq(User::getUsername, username).one();
        if (u == null || !encoder.matches(password, u.getPassword())) {
            return "redirect:/login?error=wrong_user_or_pass";
        }
        if (!u.getRole().equals(role)) {
            return "redirect:/login?error=role_mismatch";
        }

        session.setAttribute("loginUser", u);
        // 不管用户还是管理员，都先去全站文章首页
        return "redirect:/home";
    }

    @GetMapping("/register")
    public String regPage() {
        return "register";
    }

    @PostMapping("/register")
    public String reg(User u,
                      @RequestParam Integer role,
                      RedirectAttributes attr) {

        // 1. 判重
        boolean exists = userService.lambdaQuery()
                .eq(User::getUsername, u.getUsername())
                .count() > 0;
        if (exists) {
            attr.addFlashAttribute("error", "用户名已存在");
            return "redirect:/register";
        }

        // 2. 保存
        u.setPassword(encoder.encode(u.getPassword()));
        u.setRole(role);          // 关键：把页面选中的角色写进数据库
        userService.save(u);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/user/dashboard")
    public String userDashboard(HttpSession session) {
        // 验证登录状态
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/login";
        }
        return "user/dashboard";
    }
}