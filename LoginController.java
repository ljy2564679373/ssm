package edu.java.eams.controller;

import edu.java.eams.comm.RetJson;
import edu.java.eams.domain.Employee;
import edu.java.eams.domain.User;
import edu.java.eams.service.EmployeeService;
import edu.java.eams.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    private static final int EMPLOYEE_STATUS_LEFT = 3;

    @Autowired
    private LoginService loginService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/doLogin")
    @ResponseBody
    public RetJson<User> doLogin(@RequestParam("account") String account,
                                 @RequestParam("password") String password,
                                 @RequestParam(value = "loginType", required = false) String loginType,
                                 HttpServletRequest request) {
        try {
            User user = loginService.login(account, password);
            if (user == null) {
                return RetJson.error("\u8d26\u53f7\u6216\u5bc6\u7801\u9519\u8bef");
            }

            boolean isAdmin = isAdminAccount(user);
            String normalizedLoginType = normalizeLoginType(loginType);
            if (normalizedLoginType != null) {
                boolean loginAsAdmin = "admin".equals(normalizedLoginType);
                if (loginAsAdmin != isAdmin) {
                    return RetJson.error("\u6240\u9009\u767b\u5f55\u8eab\u4efd\u4e0e\u8d26\u53f7\u7c7b\u578b\u4e0d\u5339\u914d");
                }
            }

            if (!isAdmin && isLeftEmployee(user)) {
                return RetJson.error("\u60a8\u5df2\u65e0\u6743\u767b\u5f55");
            }

            HttpSession session = request.getSession();
            session.setAttribute("currentUser", user);
            session.setAttribute("isAdmin", isAdmin);
            session.setAttribute("loginType", isAdmin ? "admin" : "employee");
            return RetJson.success(user);
        } catch (Exception e) {
            return RetJson.error("\u767b\u5f55\u5f02\u5e38: " + e.getMessage());
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/login";
    }

    private boolean isAdminAccount(User user) {
        return user != null
            && user.getAccount() != null
            && "admin".equalsIgnoreCase(user.getAccount().trim());
    }

    private boolean isLeftEmployee(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }
        Employee employee = employeeService.getByUserId(user.getId());
        return employee != null && Integer.valueOf(EMPLOYEE_STATUS_LEFT).equals(employee.getStatus());
    }

    private String normalizeLoginType(String loginType) {
        if (loginType == null) {
            return null;
        }
        String normalized = loginType.trim().toLowerCase();
        if ("admin".equals(normalized) || "employee".equals(normalized)) {
            return normalized;
        }
        return null;
    }
}
