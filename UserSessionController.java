package edu.java.eams.controller;

import edu.java.eams.comm.RetJson;
import edu.java.eams.domain.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;

/**
 * 提供会话相关公共接口
 */
@Controller
public class UserSessionController {

    /**
     * 获取当前登录用户及权限信息
     */
    @GetMapping("/getCurrentUser")
    @ResponseBody
    public RetJson<Map<String, Object>> currentUser(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (user == null) {
            return RetJson.error("未登录");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("isAdmin", isAdmin != null && isAdmin);
        return RetJson.success(data);
    }
} 