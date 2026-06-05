package com.blog.config;

import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FirstVisitInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();

        // 排除路径
        if (requestUri.startsWith("/login") || requestUri.startsWith("/back")
                || requestUri.startsWith("/css") || requestUri.startsWith("/js")
                || requestUri.startsWith("/register")) {
            return true;
        }

        // 只记录第一次非排除的访问
        if (request.getSession().getAttribute("firstVisitUrl") == null) {
            String firstVisitUrl = request.getRequestURL().toString();
            String queryString = request.getQueryString();
            if (queryString != null) {
                firstVisitUrl += "?" + queryString;
            }
            request.getSession().setAttribute("firstVisitUrl", firstVisitUrl);
        }
        return true;
    }
}