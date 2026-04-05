package edu.java.eams.controller;

import edu.java.eams.comm.RetJson;
import edu.java.eams.domain.Attendance;
import edu.java.eams.domain.User;
import edu.java.eams.service.AttendanceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.beans.PropertyEditorSupport;
import java.sql.Time;
import java.util.List;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        Long filterUserId = (isAdmin != null && isAdmin) ? null : currentUser.getId();

        List<Attendance> records = (filterUserId == null)
                ? attendanceService.getAll()
                : attendanceService.getByCondition(null, filterUserId, 0, Integer.MAX_VALUE);

        model.addAttribute("records", records);
        model.addAttribute("total", records.size());
        model.addAttribute("pageNum", 1);
        model.addAttribute("pageSize", records.isEmpty() ? 10 : records.size());
        model.addAttribute("employeeName", null);
        return "attendanceList";
    }

    @GetMapping("/add")
    public String addForm(HttpSession session) {
        return "redirect:/attendance/list";
    }

    @PostMapping("/add")
    public String add(Attendance attendance, HttpSession session) {
        return "redirect:/attendance/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        return "redirect:/attendance/list";
    }

    @PostMapping("/edit")
    public String edit(Attendance attendance, HttpSession session) {
        return "redirect:/attendance/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        attendanceService.delete(id);
        return "redirect:/attendance/list";
    }

    @PostMapping("/deleteJson/{id}")
    @ResponseBody
    public RetJson<String> deleteJson(@PathVariable("id") Long id) {
        try {
            attendanceService.delete(id);
            return RetJson.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return RetJson.error("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/page")
    public String page(@RequestParam(value = "employeeName", required = false) String employeeName,
                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                       HttpSession session,
                       Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        int start = (pageNum - 1) * pageSize;
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        Long filterUserId = (isAdmin != null && isAdmin) ? null : currentUser.getId();

        List<Attendance> records = attendanceService.getByCondition(employeeName, filterUserId, start, pageSize);
        int total = attendanceService.countByCondition(employeeName, filterUserId);

        model.addAttribute("records", records);
        model.addAttribute("total", total);
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("employeeName", employeeName);
        return "attendanceList";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        PropertyEditorRegistry registry = binder;
        registry.registerCustomEditor(Time.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.isEmpty()) {
                    setValue(null);
                    return;
                }
                if (text.length() == 5) {
                    text = text + ":00";
                }
                setValue(Time.valueOf(text));
            }
        });
    }

    @PostMapping("/check-in")
    @ResponseBody
    public RetJson<String> checkIn(HttpSession session) {
        try {
            User user = (User) session.getAttribute("currentUser");
            if (user == null) {
                return RetJson.error("未登录");
            }
            boolean ok = attendanceService.checkIn(user.getId(), user.getName());
            return ok ? RetJson.success("签到成功") : RetJson.error("您今天已经签到过了");
        } catch (Exception e) {
            e.printStackTrace();
            return RetJson.error("签到失败: " + e.getMessage());
        }
    }

    @PostMapping("/check-out")
    @ResponseBody
    public RetJson<String> checkOut(HttpSession session) {
        try {
            User user = (User) session.getAttribute("currentUser");
            if (user == null) {
                return RetJson.error("未登录");
            }
            boolean ok = attendanceService.checkOut(user.getId(), user.getName());
            return ok ? RetJson.success("签退成功") : RetJson.error("您今天已经签退过了");
        } catch (Exception e) {
            e.printStackTrace();
            return RetJson.error("签退失败: " + e.getMessage());
        }
    }
}
