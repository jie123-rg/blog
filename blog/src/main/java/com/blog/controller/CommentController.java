package com.blog.controller;

import com.blog.entity.Comment;
import com.blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment")
    public String add(@RequestParam Long articleId,
                      @RequestParam String content,
                      HttpSession session) {
        Comment c = new Comment();
        c.setArticleId(articleId);
        c.setUserId(((com.blog.entity.User) session.getAttribute("loginUser")).getId());
        c.setContent(content);
        commentService.save(c);
        return "redirect:/article/" + articleId;
    }

}