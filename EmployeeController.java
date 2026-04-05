package edu.java.eams.controller;

import edu.java.eams.domain.Employee;
import edu.java.eams.service.EmployeeService;
import edu.java.eams.service.DepartmentService;
import edu.java.eams.service.PostLevelService;
import edu.java.eams.comm.RetJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.sql.Date;
import java.util.*;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired private EmployeeService employeeService;
    @Autowired private DepartmentService departmentService;
    @Autowired private PostLevelService postLevelService;
    @Autowired private JdbcTemplate jdbcTemplate;


    // ==================== 管理员：员工+用户合并列表 ====================

    @GetMapping("/list")
    public String list() { return "redirect:/employee/page"; }

    /**
     * 员工管理主页（含用户账号信息，合并视图）
     */
    @GetMapping("/page")
    public String page(
            @RequestParam(value = "kw", required = false) String kw,
            @RequestParam(value = "statusFilter", required = false) String statusFilter,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            Model model) {

        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1) pageSize = 10;
        int start = (pageNum - 1) * pageSize;

        List<Object> params = new ArrayList<>();
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        if (kw != null && !kw.trim().isEmpty()) {
            where.append(" AND (e.name LIKE ? OR e.code LIKE ?)");
            params.add("%" + kw.trim() + "%");
            params.add("%" + kw.trim() + "%");
        }
        if (statusFilter != null && !statusFilter.isEmpty()) {
            where.append(" AND e.status = ?");
            params.add(Integer.parseInt(statusFilter));
        }

        String baseSql = "FROM emp e LEFT JOIN sys_user u ON e.user_id=u.id"
                       + " LEFT JOIN sys_dept d ON e.dept_id=d.id" + where;

        String listSql = "SELECT e.id, e.name, e.code, e.email, e.mobile, e.status AS emp_status,"
                       + " e.dept_id, e.post_level_id, e.entry_date, e.user_id,"
                       + " u.account, d.name AS dept_name " + baseSql
                       + " ORDER BY e.id DESC LIMIT ?,?";
        List<Object> listParams = new ArrayList<>(params);
        listParams.add(start);
        listParams.add(pageSize);

        String countSql = "SELECT COUNT(*) " + baseSql;

        List<Map<String, Object>> employees = jdbcTemplate.queryForList(listSql, listParams.toArray());
        int total = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());

        List<Map<String, Object>> depts = jdbcTemplate.queryForList(
                "SELECT id, name FROM sys_dept WHERE status=1 ORDER BY sort_code");
        List<Map<String, Object>> postLevels = jdbcTemplate.queryForList(
                "SELECT id, name, level FROM emp_post_level WHERE status=1 ORDER BY sort_code");

        model.addAttribute("employees", employees);
        model.addAttribute("total", total);
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("kw", kw);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("depts", depts);
        model.addAttribute("postLevels", postLevels);
        return "employeeList";
    }

    /**
     * 获取单条员工信息（含账号），用于编辑弹窗回显
     */
    @GetMapping("/editJson/{id}")
    @ResponseBody
    public RetJson<Map<String, Object>> editJson(@PathVariable("id") Long id) {
        try {
            String sql = "SELECT e.id, e.name, e.code, e.email, e.mobile, e.status,"
                       + " e.dept_id, e.post_level_id, e.entry_date, e.user_id, u.account"
                       + " FROM emp e LEFT JOIN sys_user u ON e.user_id=u.id WHERE e.id=?";
            Map<String, Object> emp = jdbcTemplate.queryForMap(sql, id);
            return RetJson.success(emp);
        } catch (Exception e) {
            e.printStackTrace();
            return RetJson.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * AJAX 更新员工信息（编辑弹窗"确认修改"）
     */
    @PostMapping("/updateJson")
    @ResponseBody
    @Transactional
    public RetJson<String> updateJson(@RequestParam Map<String, String> params) {
        try {
            Long id       = Long.parseLong(params.get("id"));
            String name   = params.get("name");
            String code   = params.get("code");
            String email  = params.get("email");
            String mobile = params.get("mobile");
            String deptIdStr    = params.get("deptId");
            String postLevelStr = params.get("postLevelId");
            String entryDateStr = params.get("entryDate");
            String statusStr    = params.get("status");

            Long deptId      = (deptIdStr != null && !deptIdStr.isEmpty()) ? Long.parseLong(deptIdStr) : null;
            Long postLevelId = (postLevelStr != null && !postLevelStr.isEmpty()) ? Long.parseLong(postLevelStr) : null;
            Date entryDate   = parseSqlDate(entryDateStr);
            Integer status   = (statusStr != null && !statusStr.isEmpty()) ? Integer.parseInt(statusStr) : 1;

            Map<String, Object> current = jdbcTemplate.queryForMap(
                "SELECT code, user_id FROM emp WHERE id=?",
                id);
            String currentCode = current.get("code") == null ? "" : current.get("code").toString().trim();
            String requestCode = code == null ? "" : code.trim();
            if (!requestCode.isEmpty() && !currentCode.equals(requestCode)) {
                return RetJson.error("不允许修改工号，员工添加后工号固定");
            }

            jdbcTemplate.update(
                "UPDATE emp SET name=?,email=?,mobile=?,dept_id=?,post_level_id=?,entry_date=?,status=? WHERE id=?",
                name, email, mobile, deptId, postLevelId, entryDate, status, id);

            // 工号固定后，仅同步 sys_user 的姓名
            Long userId = toLong(current.get("user_id"));
            if (userId != null) {
                jdbcTemplate.update("UPDATE sys_user SET name=? WHERE id=?", name, userId);
            }
            return RetJson.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return RetJson.error("修改失败：" + e.getMessage());
        }
    }

    /**
     * 添加员工并自动创建登录账号（工号=账号，默认密码123456）
     */
    @PostMapping("/addWithUser")
    @ResponseBody
    @Transactional
    public RetJson<String> addWithUser(@RequestParam Map<String, String> params) {
        try {
            String name   = params.get("name");
            String code   = params.get("code");
            String email  = params.get("email");
            String mobile = params.get("mobile");
            String deptIdStr    = params.get("deptId");
            String postLevelStr = params.get("postLevelId");
            String statusStr    = params.get("status");
            String entryDate    = params.get("entryDate");

            if (name == null || name.trim().isEmpty()) return RetJson.error("姓名不能为空");
            if (code == null || code.trim().isEmpty())  return RetJson.error("工号不能为空");

            Integer codeCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM emp WHERE code=?", Integer.class, code);
            if (codeCount != null && codeCount > 0) return RetJson.error("工号已存在");

            Integer acctCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM sys_user WHERE account=?", Integer.class, code);
            if (acctCount != null && acctCount > 0) return RetJson.error("该账号已存在");

            if (entryDate == null || entryDate.trim().isEmpty()) return RetJson.error("璇烽€夋嫨鍏ヨ亴鏃ユ湡");

            Long deptId      = (deptIdStr != null && !deptIdStr.isEmpty()) ? Long.parseLong(deptIdStr) : null;
            Long postLevelId = (postLevelStr != null && !postLevelStr.isEmpty()) ? Long.parseLong(postLevelStr) : null;
            Date entryDateValue = parseSqlDate(entryDate);
            Integer status   = (statusStr != null && !statusStr.isEmpty()) ? Integer.parseInt(statusStr) : 1;

            // 1. 创建 sys_user（账号=工号，密码=123456）
            jdbcTemplate.update(
                "INSERT INTO sys_user(name,account,password,status) VALUES(?,?,?,?)",
                name, code, "123456", 1);
            Long userId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

            // 2. 创建 emp
            jdbcTemplate.update(
                "INSERT INTO emp(name,code,email,mobile,dept_id,post_level_id,status,user_id,role_type,entry_date)"
              + " VALUES(?,?,?,?,?,?,?,?,?,?)",
                name, code, email, mobile, deptId, postLevelId, status, userId, 3, entryDateValue);

            return RetJson.success("添加成功，初始密码为 123456");
        } catch (Exception e) {
            e.printStackTrace();
            return RetJson.error("添加失败：" + e.getMessage());
        }
    }

    /**
     * AJAX 删除员工
     */
    @PostMapping("/deleteJson/{id}")
    @ResponseBody
    public RetJson<String> deleteJson(@PathVariable("id") Long id) {
        try {
            employeeService.deleteEmployee(id);
            return RetJson.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return RetJson.error("删除失败：" + e.getMessage());
        }
    }

    // ==================== 旧版表单路由（保留兼容） ====================

    @GetMapping("/add")
    public String addPage() { return "employeeAdd"; }

    @PostMapping("/add")
    public String add(Employee employee) {
        employeeService.addEmployee(employee);
        return "redirect:/employee/list";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(id));
        return "employeeEdit";
    }

    @PostMapping("/edit")
    public String edit(Employee employee) {
        employeeService.updateEmployee(employee);
        return "redirect:/employee/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        return "redirect:/employee/list";
    }

    // ==================== 员工端：个人信息 ====================

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        edu.java.eams.domain.User user = (edu.java.eams.domain.User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        Employee employee = employeeService.getByUserId(user.getId());
        model.addAttribute("employee", employee);
        model.addAttribute("currentUserId", user.getId());
        model.addAttribute("currentUserAccount", user.getAccount()); // 工号（只读）

        if (employee != null) {
            if (employee.getDeptId() != null) {
                try {
                    edu.java.eams.domain.Department d = departmentService.getDepartmentById(employee.getDeptId());
                    if (d != null) model.addAttribute("deptName", d.getName());
                } catch (Exception ignored) {}
            }
            if (employee.getPostLevelId() != null) {
                try {
                    edu.java.eams.domain.PostLevel pl = postLevelService.getPostLevelById(employee.getPostLevelId());
                    if (pl != null) model.addAttribute("postLevelName", pl.getName());
                } catch (Exception ignored) {}
            }
        }
        return "employeeProfile";
    }

    @PostMapping("/updateProfile")
    @ResponseBody
    public RetJson<String> updateProfile(Employee employee, HttpSession session) {
        try {
            edu.java.eams.domain.User user = (edu.java.eams.domain.User) session.getAttribute("currentUser");
            if (user == null) return RetJson.error("未登录");
            Employee cur = employeeService.getByUserId(user.getId());
            if (cur == null) return RetJson.error("未找到员工信息");
            if (!cur.getId().equals(employee.getId())) return RetJson.error("无权修改");

            cur.setName(employee.getName());
            cur.setEmail(employee.getEmail());
            cur.setMobile(employee.getMobile());
            cur.setHabitation(employee.getHabitation());
            cur.setSex(employee.getSex());
            cur.setBirthday(employee.getBirthday());
            cur.setIdCard(employee.getIdCard());
            cur.setPoliticalCode(employee.getPoliticalCode());
            cur.setGraduateSchool(employee.getGraduateSchool());
            cur.setMajorCode(employee.getMajorCode());
            cur.setHighestEducationCode(employee.getHighestEducationCode());

            boolean ok = employeeService.updateEmployee(cur);
            if (ok) jdbcTemplate.update("UPDATE sys_user SET name=? WHERE id=?", cur.getName(), user.getId());
            return ok ? RetJson.success("保存成功") : RetJson.error("保存失败");
        } catch (Exception e) {
            e.printStackTrace();
            return RetJson.error("保存失败：" + e.getMessage());
        }
    }

    @PostMapping("/createProfile")
    @ResponseBody
    @Transactional
    public RetJson<String> createProfile(Employee employee, HttpSession session) {
        try {
            edu.java.eams.domain.User user = (edu.java.eams.domain.User) session.getAttribute("currentUser");
            if (user == null) return RetJson.error("未登录");
            if (employeeService.getByUserId(user.getId()) != null) return RetJson.error("您已有员工档案");

            employee.setUserId(user.getId());
            employee.setCode(user.getAccount()); // 工号 = 登录账号
            if (employee.getStatus() == null) employee.setStatus(1);
            if (employee.getRoleType() == null) employee.setRoleType(3);

            boolean ok = employeeService.addEmployee(employee);
            if (ok && employee.getName() != null)
                jdbcTemplate.update("UPDATE sys_user SET name=? WHERE id=?", employee.getName(), user.getId());
            return ok ? RetJson.success("档案创建成功") : RetJson.error("创建失败");
        } catch (Exception e) {
            e.printStackTrace();
            return RetJson.error("创建失败：" + e.getMessage());
        }
    }

    private Date parseSqlDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Date.valueOf(value.trim());
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());
    }
}
