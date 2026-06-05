// blog/src/main/java/com/blog/service/impl/CategoryServiceImpl.java
package com.blog.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Category;
import com.blog.mapper.CategoryMapper;
import com.blog.service.ArticleService;
import com.blog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final ArticleService articleService;

    // 检查分类下是否有文章
    public boolean hasArticles(Long categoryId) {
        return articleService.lambdaQuery()
                .eq(categoryId != null, article -> article.getCategoryId(), categoryId)
                .exists();
    }
}