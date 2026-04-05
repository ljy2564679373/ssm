package edu.java.eams.controller;

import edu.java.eams.comm.RetJson;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/salary")
public class SalaryController {

    private static final String SALARY_INSERT_SQL =
            "INSERT INTO emp_salary(user_id,user_name,pay_month,amount,remark,create_time) VALUES(?,?,?,?,?,?)";

    private static final String EMPLOYEE_LOOKUP_SQL =
            "SELECT u.id AS user_id, u.name AS user_name, e.code AS emp_code, e.name AS emp_name"
          + " FROM sys_user u"
          + " JOIN emp e ON e.user_id=u.id";

    private static final String DEPT_CHART_SQL =
            "SELECT d.name AS dept_name, SUM(sm.total_amount) AS total"
          + " FROM ("
          + "   SELECT user_id, SUM(amount) AS total_amount"
          + "   FROM emp_salary"
          + "   WHERE pay_month=?"
          + "   GROUP BY user_id"
          + " ) sm"
          + " JOIN emp e ON e.user_id=sm.user_id"
          + " JOIN sys_dept d ON e.dept_id=d.id"
          + " GROUP BY d.id, d.name"
          + " ORDER BY total DESC";

    private static final String YEAR_CHART_SQL =
            "SELECT pay_month, SUM(amount) AS total"
          + " FROM emp_salary"
          + " WHERE pay_month>=? AND pay_month<=?"
          + " GROUP BY pay_month"
          + " ORDER BY pay_month";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 管理员查看薪资列表
     */
    @GetMapping("/list")
    public String list(Model model) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "SELECT s.id, s.user_id, s.user_name, s.pay_month, s.amount, s.remark, s.create_time"
              + " FROM emp_salary s ORDER BY s.pay_month DESC, s.create_time DESC");
        model.addAttribute("records", list);
        // 当前年份（默认图表年份）
        model.addAttribute("curYear", java.time.Year.now().getValue());
        // 当前月份（默认图表月份）
        model.addAttribute("curMonth", String.format("%d-%02d",
                java.time.LocalDate.now().getYear(), java.time.LocalDate.now().getMonthValue()));
        return "salaryList";
    }

    /**
     * 管理员新增单条薪资记录（保留原功能）
     */
    @PostMapping("/add")
    @ResponseBody
    public RetJson<String> addJson(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam("userName") String userName,
            @RequestParam("payMonth") String payMonth,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam(value = "remark", required = false) String remark) {
        try {
            if (userId == null) {
                List<Long> ids = jdbcTemplate.queryForList(
                        "SELECT u.id FROM sys_user u JOIN emp e ON e.user_id=u.id WHERE e.name=? OR u.name=? LIMIT 1",
                        Long.class, userName, userName);
                if (ids.isEmpty()) return RetJson.error("未找到员工：" + userName);
                userId = ids.get(0);
            }
            jdbcTemplate.update(
                SALARY_INSERT_SQL,
                userId, userName, payMonth, amount, remark, Timestamp.valueOf(LocalDateTime.now()));
            return RetJson.success("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return RetJson.error("添加失败：" + e.getMessage());
        }
    }

    /**
     * Excel 批量导入薪资（xlsx格式）
     * Excel表头：员工姓名 | 工号 | 发放月份(YYYY-MM) | 金额 | 备注(可选)
     */
    @PostMapping("/import")
    @ResponseBody
    public RetJson<Map<String, Object>> importSalary(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return RetJson.error("请选择文件");
        int success = 0, fail = 0;
        List<String> errors = new ArrayList<>();
        try (InputStream is = file.getInputStream();
             Workbook wb = new XSSFWorkbook(is)) {
            Map<String, Map<String, Object>> employeesByCode = new HashMap<>();
            Map<String, Map<String, Object>> employeesByName = new HashMap<>();
            for (Map<String, Object> employee : jdbcTemplate.queryForList(EMPLOYEE_LOOKUP_SQL)) {
                String empCode = textOf(employee.get("emp_code"));
                String empName = textOf(employee.get("emp_name"));
                if (!empCode.isEmpty()) {
                    employeesByCode.put(empCode, employee);
                }
                if (!empName.isEmpty() && !employeesByName.containsKey(empName)) {
                    employeesByName.put(empName, employee);
                }
            }

            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {  // 跳过第一行表头
                Row row = sheet.getRow(i);
                if (row == null) continue;
                try {
                    String empName  = getCellStr(row.getCell(0));
                    String code     = getCellStr(row.getCell(1));
                    String payMonth = getCellStr(row.getCell(2));
                    String amtStr   = getCellStr(row.getCell(3));
                    String remark   = getCellStr(row.getCell(4));

                    if (empName.isEmpty() && code.isEmpty()) { errors.add("第" + (i+1) + "行：姓名和工号均为空"); fail++; continue; }
                    if (payMonth.isEmpty()) { errors.add("第" + (i+1) + "行：发放月份为空"); fail++; continue; }
                    if (amtStr.isEmpty())   { errors.add("第" + (i+1) + "行：金额为空"); fail++; continue; }

                    BigDecimal amount = new BigDecimal(amtStr);

                    // 员工映射只查一次，避免导入时逐行访问数据库
                    Map<String, Object> employee = null;
                    if (!code.isEmpty()) {
                        employee = employeesByCode.get(code);
                    }
                    if (employee == null && !empName.isEmpty()) {
                        employee = employeesByName.get(empName);
                    }
                    if (employee == null) { errors.add("第" + (i+1) + "行：未找到员工 [" + empName + "/" + code + "]"); fail++; continue; }

                    jdbcTemplate.update(
                        SALARY_INSERT_SQL,
                        toLong(employee.get("user_id")),
                        textOf(employee.get("user_name")),
                        payMonth,
                        amount,
                        remark,
                        Timestamp.valueOf(LocalDateTime.now()));
                    success++;
                } catch (Exception ex) {
                    errors.add("第" + (i+1) + "行：" + ex.getMessage());
                    fail++;
                }
            }
        } catch (Exception e) {
            return RetJson.error("文件解析失败：" + e.getMessage());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("fail", fail);
        result.put("errors", errors);
        return RetJson.success(result);
    }

    /**
     * 下载 Excel 模板
     */
    @GetMapping("/export")
    public void exportSalary(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=salary_records.xlsx");

        List<Map<String, Object>> records = jdbcTemplate.queryForList(
                "SELECT s.user_name, e.code AS emp_code, s.pay_month, s.amount, s.remark, s.create_time"
              + " FROM emp_salary s"
              + " LEFT JOIN emp e ON e.user_id = s.user_id"
              + " ORDER BY s.pay_month DESC, s.create_time DESC");

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("salary_records");
            String[] cols = {"员工姓名", "工号", "发放月份", "金额", "备注", "录入时间"};

            Row header = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                sheet.setColumnWidth(i, 5000);
            }

            for (int i = 0; i < records.size(); i++) {
                Map<String, Object> record = records.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(textOf(record.get("user_name")));
                row.createCell(1).setCellValue(textOf(record.get("emp_code")));
                row.createCell(2).setCellValue(textOf(record.get("pay_month")));

                Object amount = record.get("amount");
                if (amount instanceof BigDecimal) {
                    row.createCell(3).setCellValue(((BigDecimal) amount).doubleValue());
                } else if (amount instanceof Number) {
                    row.createCell(3).setCellValue(((Number) amount).doubleValue());
                } else {
                    row.createCell(3).setCellValue(textOf(amount));
                }

                row.createCell(4).setCellValue(textOf(record.get("remark")));
                row.createCell(5).setCellValue(textOf(record.get("create_time")));
            }

            wb.write(response.getOutputStream());
        }
    }

    /**
     * 下载 Excel 模板
     */
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=salary_template.xlsx");
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("薪资导入模板");
            Row header = sheet.createRow(0);
            String[] cols = {"员工姓名", "工号", "发放月份(YYYY-MM)", "金额", "备注"};
            for (int i = 0; i < cols.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                sheet.setColumnWidth(i, 5000);
            }
            // 示例行
            Row demo = sheet.createRow(1);
            demo.createCell(0).setCellValue("张三");
            demo.createCell(1).setCellValue("EMP001");
            demo.createCell(2).setCellValue("2026-03");
            demo.createCell(3).setCellValue(8000);
            demo.createCell(4).setCellValue("正常发放");
            wb.write(response.getOutputStream());
        }
    }

    /**
     * AJAX 删除薪资记录
     */
    @PostMapping("/deleteJson/{id}")
    @ResponseBody
    public RetJson<String> deleteJson(@PathVariable("id") Long id) {
        try {
            jdbcTemplate.update("DELETE FROM emp_salary WHERE id=?", id);
            return RetJson.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return RetJson.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 图表数据：指定月份各部门发薪总额
     * GET /salary/chartByDept?month=2026-03
     */
    @GetMapping("/chartByDept")
    @ResponseBody
    public RetJson<Map<String, Object>> chartByDept(@RequestParam("month") String month) {
        if (!month.matches("\\d{4}-\\d{2}")) {
            return RetJson.error("月份格式错误，应为 YYYY-MM");
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
            DEPT_CHART_SQL, month);
        List<String> labels = new ArrayList<>();
        List<Object> data   = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            labels.add((String) r.get("dept_name"));
            data.add(r.get("total"));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("data", data);
        return RetJson.success(result);
    }

    /**
     * 图表数据：指定年份每月薪资总额
     * GET /salary/chartByYear?year=2026
     */
    @GetMapping("/chartByYear")
    @ResponseBody
    public RetJson<Map<String, Object>> chartByYear(@RequestParam("year") String year) {
        if (!year.matches("\\d{4}")) {
            return RetJson.error("年份格式错误，应为 YYYY");
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
            YEAR_CHART_SQL, year + "-01", year + "-12");
        List<String> labels = new ArrayList<>();
        List<Object> data   = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            String m = (String) r.get("pay_month");
            labels.add(m.substring(5) + "月"); // "03" -> "03月"
            data.add(r.get("total"));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("data", data);
        return RetJson.success(result);
    }

    /**
     * 员工查看自己的薪资记录
     */
    @GetMapping("/my")
    public String mySalary(jakarta.servlet.http.HttpSession session, Model model) {
        edu.java.eams.domain.User user = (edu.java.eams.domain.User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "SELECT pay_month AS payMonth, amount, remark, create_time AS createTime"
              + " FROM emp_salary WHERE user_id=? ORDER BY pay_month DESC, create_time DESC", user.getId());
        model.addAttribute("records", list);
        return "mySalary";
    }

    // ===== 工具方法 =====
    private String getCellStr(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return new java.text.SimpleDateFormat("yyyy-MM").format(cell.getDateCellValue());
            }
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return cell.toString().trim();
    }

    private Long toLong(Object o) {
        if (o == null) return null;
        if (o instanceof Long) return (Long) o;
        if (o instanceof Number) return ((Number) o).longValue();
        return Long.parseLong(o.toString());
    }

    private String textOf(Object o) {
        return o == null ? "" : o.toString().trim();
    }
}
