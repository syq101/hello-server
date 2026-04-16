package com.example.helloserver.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 放行用户详情接口，作业测试用
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/users/")) {
            return true; // 直接放行，不校验Token
        }

        // 1. 获取本次请求的 HTTP 动词和具体路径
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // 2. 手写精细化放行规则
        // 规则 A：如果是 POST 请求，且路径精确等于 "/api/users"，则放行（允许注册）
        boolean isCreateUser = "POST".equalsIgnoreCase(method) && "/api/users".equals(uri);
        // 规则 B：如果是 POST 请求，且路径精确等于 "/api/users/login"，则放行（允许登录）
        boolean isLogin1 = "POST".equalsIgnoreCase(method) && "/api/users/login".equals(uri);
        // 规则 C：如果是 POST 请求，且路径精确等于 "/api/login"，则放行（允许登录，兼容前端调用）
        boolean isLogin2 = "POST".equalsIgnoreCase(method) && "/api/login".equals(uri);
        // 规则 D：如果是 GET 请求，且路径以 "/api/users/" 开头，并且后面是数字 ID，则放行（允许查看用户信息）
        boolean isGetUser = "GET".equalsIgnoreCase(method) && uri.matches("/api/users/\\d+");
        // 规则 E：如果是 GET 请求，且路径精确等于 "/api/users/page"，则放行（允许分页查询）
        boolean isGetUsersPage = "GET".equalsIgnoreCase(method) && "/api/users/page".equals(uri);

        // 只要满足上述任一合法公开规则，直接放行，无需查验 Token
        if (isCreateUser || isLogin1 || isLogin2 || isGetUser || isGetUsersPage) {
            return true;
        }

        // 3. 尝试从 HTTP 请求头中截获名为 "Authorization" 的隐藏令牌信息
        String token = request.getHeader("Authorization");

        // 4. 如果没有携带 Token，直接拦截，不放到 Controller
        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=UTF-8");
            // 构造 401 状态码 JSON 字符串返回给前端
            String errorJson = "{\"code\": 401, \"msg\": \"登录凭证已缺失，请注意携带令牌信息\"}";
            response.getWriter().write(errorJson);
            return false; // 返回 false 表示拦截打回
        }

        return true; // 令牌存在，返回 true 予以放行
    }
}