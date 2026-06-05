package com.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Article;

// ArticleService接口
public interface ArticleService extends IService<Article> {
    IPage<Article> pageFront(Integer current, Long categoryId);
    void incrViewNum(Long id);
    // 添加这个方法定义
    boolean removeArticleWithComment(Long id);
}