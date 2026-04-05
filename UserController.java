package edu.java.eams.controller;

import edu.java.eams.domain.User;
import edu.java.eams.service.UserService;
import edu.java.eams.comm.RetJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public String list() {
        return "redirect:/employee/page";   // 合并后跳转到员工管理页
    }

    @GetMapping("/add")
    public String addForm() {
        return "userAdd";
    }

    @PostMapping("/add")
    public String add(User user) {
        userService.addUser(user);
        return "redirect:/employee/page";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "userEdit";
    }

    @PostMapping("/edit")
    public String edit(User user) {
        userService.updateUser(user);
        return "redirect:/employee/page";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/employee/page";
    }

    @PostMapping("/deleteJson/{id}")
    @ResponseBody
    public RetJson<String> deleteJson(@PathVariable("id") Long id) {
        try {
            userService.deleteUser(id);
            return RetJson.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return RetJson.error("删除失败：" + e.getMessage());
        }
    }

    @GetMapping("/page")
    public String page(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
        Model model) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int start = (pageNum - 1) * pageSize;
        if (start < 0) start = 0;
        List<User> users = userService.getUsersByCondition(name, start, pageSize);
        int total = userService.countUsersByCondition(name);
        model.addAttribute("users", users);
        model.addAttribute("total", total);
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("name", name);
        return "userList";
    }

    /**
     * 修改密码（员工和管理员均可访问）
     */
    @PostMapping("/changePassword")
    @ResponseBody
    public RetJson<String> changePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("confirmOldPassword") String confirmOldPassword,
            @RequestParam("newPassword") String newPassword,
            HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                return RetJson.error("未登录，请先登录");
            }
            if (!oldPassword.equals(confirmOldPassword)) {
                return RetJson.error("两次输入的原密码不一致");
            }
            if (newPassword.length() < 6) {
                return RetJson.error("新密码长度不能少于6位");
            }
            // 验证原密码
            User check = userService.getUserById(currentUser.getId());
            if (check == null || !oldPassword.equals(check.getPassword())) {
                return RetJson.error("原密码错误");
            }
            // 更新密码
            check.setPassword(newPassword);
            userService.updateUser(check);
            // 同步更新session中的用户信息
            currentUser.setPassword(newPassword);
            session.setAttribute("currentUser", currentUser);
            return RetJson.success("密码修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return RetJson.error("修改失败：" + e.getMessage());
        }
    }
}
