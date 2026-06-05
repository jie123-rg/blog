package com.blog.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.CommentMapper;
import com.blog.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable; // 导入Serializable接口

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private CommentMapper commentMapper;

    // 原有方法不变
    @Override
    public IPage<Article> pageFront(Integer current, Long categoryId) {
        LambdaQueryWrapper<Article> q = new LambdaQueryWrapper<>();
        q.eq(Article::getStatus, 1)
                .eq(categoryId != null, Article::getCategoryId, categoryId)
                .orderByDesc(Article::getCreateTime);
        return page(new Page<>(current, 5), q);
    }

    @Override
    public void incrViewNum(Long id) {
        baseMapper.incrView(id);
    }

    // 自定义删除方法（无@Override，因为是新增方法）
    @Transactional
    public boolean removeArticleWithComment(Long id) {
        // 先删评论
        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(Comment::getArticleId, id);
        commentMapper.delete(commentWrapper);
        // 再删文章
        return this.removeById(id);
    }

    // 修复：重写removeById，参数改为Serializable，匹配父类签名
    @Override
    @Transactional
    public boolean removeById(Serializable id) { // 关键：参数是Serializable，不是Long
        // 先删该文章的评论（id强转为Long，因为文章ID是Long类型）
        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(Comment::getArticleId, (Long) id);
        commentMapper.delete(commentWrapper);
        // 调用父类的removeById方法
        return super.removeById(id);
    }
}