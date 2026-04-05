package edu.java.eams.Interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 权限拦截器：限制普通用户访问管理员功能
 */
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        String uri = request.getRequestURI();

        // 普通用户也可访问的路径
        String[] allowedPaths = {
            "/employee/profile",
            "/employee/updateProfile",
            "/employee/createProfile",
            "/user/changePassword"      // 修改自己的密码
        };
        for (String p : allowedPaths) {
            if (uri.contains(p)) return true;
        }

        // 需要管理员权限的路径前缀
        String[] adminPaths = { "/department/", "/postlevel/", "/employee/", "/user/" };
        boolean isAdminPath = false;
        for (String p : adminPaths) {
            if (uri.contains(p)) { isAdminPath = true; break; }
        }

        if (isAdminPath && (isAdmin == null || !isAdmin)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println("<html><head><title>访问被拒绝</title>"
                + "<link rel='stylesheet' href='" + request.getContextPath() + "/static/bootstrap/css/bootstrap.min.css'/>"
                + "</head><body style='text-align:center;margin-top:120px;'>"
                + "<h3 class='text-danger'>&#128683; 无权限访问</h3>"
                + "<p class='text-muted'>您没有权限访问此功能，请联系管理员。</p>"
                + "<button class='btn btn-secondary' onclick='history.back()'>返回</button>"
                + "</body></html>");
            return false;
        }
        return true;
    }
}
