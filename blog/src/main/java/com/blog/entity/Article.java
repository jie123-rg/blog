package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private Long categoryId;
    private Long authorId;
    private Long viewNum;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}