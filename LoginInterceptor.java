package edu.java.eams.Interceptor;

import edu.java.eams.domain.User;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 登录拦截器
 * 拦截未登录用户访问系统功能
 */
public class LoginInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        // 如果用户未登录，重定向到登录页面
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        
        return true;
    }
} 