package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Article;
import org.apache.ibatis.annotations.Update;

public interface ArticleMapper extends BaseMapper<Article> {
    @Update("UPDATE t_article SET view_num = view_num + 1 WHERE id = #{id}")
    void incrView(Long id);
}