package com.blog.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class R {
    private boolean success;
    public static R ok() { return R.of(true); }
}