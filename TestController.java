package edu.java.eams.controller;

import edu.java.eams.comm.RetJson;
import edu.java.eams.domain.User;
import edu.java.eams.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试控制器
 * 用于验证系统各组件是否正常工作
 */
@Controller
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 测试数据库连接
     */
    @GetMapping("/db")
    @ResponseBody
    public RetJson<Map<String, Object>> testDatabase() {
        try {
            List<User> users = userMapper.findAll();
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "数据库连接正常");
            result.put("userCount", users.size());
            result.put("users", users);
            
            return RetJson.success(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "数据库连接失败");
            error.put("error", e.getMessage());
            
            return RetJson.error("数据库连接失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试Spring配置
     */
    @GetMapping("/spring")
    @ResponseBody
    public RetJson<Map<String, Object>> testSpring() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Spring配置正常");
        result.put("userMapper", userMapper != null ? "已注入" : "未注入");
        result.put("timestamp", System.currentTimeMillis());
        
        return RetJson.success(result);
    }
    
    /**
     * 测试登录功能
     */
    @GetMapping("/login")
    @ResponseBody
    public RetJson<Map<String, Object>> testLogin() {
        try {
            // 测试查找管理员用户
            User admin = userMapper.findByAccountAndPassword("admin", "123456");
            User user = userMapper.findByAccountAndPassword("user", "123456");
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "登录功能测试");
            result.put("adminFound", admin != null);
            result.put("userFound", user != null);
            
            if (admin != null) {
                result.put("adminName", admin.getName());
            }
            if (user != null) {
                result.put("userName", user.getName());
            }
            
            return RetJson.success(result);
        } catch (Exception e) {
            return RetJson.error("登录功能测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 系统信息
     */
    @GetMapping("/info")
    @ResponseBody
    public RetJson<Map<String, Object>> systemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("projectName", "员工考勤管理系统");
        info.put("version", "1.0.0");
        info.put("framework", "SSM (Spring + SpringMVC + MyBatis)");
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("osName", System.getProperty("os.name"));
        info.put("serverTime", new java.util.Date());
        
        return RetJson.success(info);
    }
} 