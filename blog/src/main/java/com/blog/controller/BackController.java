package com.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpSession;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpSession;

@Controller  // 改为 @Controller
public class BackController {

    @GetMapping("/back")
    public String backToFirstPage(HttpSession session) {
        String firstVisitUrl = (String) session.getAttribute("firstVisitUrl");

        if (firstVisitUrl != null) {
            return "redirect:" + firstVisitUrl;
        } else {
            return "redirect:/";
        }
    }
}
