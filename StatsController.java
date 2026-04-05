package edu.java.eams.controller;

import edu.java.eams.comm.RetJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统首页统计数据
 */
@RestController
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/summary")
    public RetJson<Map<String,Object>> summary(){
        Map<String,Object> data = new HashMap<>();
        try{
            Long userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sys_user", Long.class);
            Long empCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM emp", Long.class);
            Long deptCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sys_dept", Long.class);
            Long todayAtt = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM emp_attendance_record WHERE attendance_date = CURRENT_DATE", Long.class);
            data.put("userCount", userCount);
            data.put("employeeCount", empCount);
            data.put("departmentCount", deptCount);
            data.put("todayAttendance", todayAtt);
            return RetJson.success(data);
        }catch(Exception e){
            e.printStackTrace();
            return RetJson.error("统计查询失败:"+e.getMessage());
        }
    }

    @GetMapping("/employee-sex")
    public RetJson<List<Map<String,Object>>> employeeSex(){
        try{
            List<Map<String,Object>> list = jdbcTemplate.query("SELECT CASE WHEN sex=1 THEN '男' WHEN sex=0 THEN '女' ELSE '未知' END AS name, COUNT(*) AS value FROM emp GROUP BY sex", (rs,i)->{
                Map<String,Object> m=new HashMap<>();
                m.put("name", rs.getString("name"));
                m.put("value", rs.getLong("value"));
                return m;
            });
            return RetJson.success(list);
        }catch (Exception e){
            e.printStackTrace();
            return RetJson.error("查询员工性别统计失败:"+e.getMessage());
        }
    }

    @GetMapping("/dept-employee")
    public RetJson<List<Map<String,Object>>> deptEmployee(){
        try{
            List<Map<String,Object>> list = jdbcTemplate.query("SELECT d.name AS name, COUNT(e.id) AS value FROM sys_dept d LEFT JOIN emp e ON e.dept_id = d.id GROUP BY d.name", (rs,i)->{
                Map<String,Object> m=new HashMap<>();
                m.put("name", rs.getString("name"));
                m.put("value", rs.getLong("value"));
                return m;
            });
            return RetJson.success(list);
        }catch(Exception e){
            e.printStackTrace();
            return RetJson.error("查询部门人数统计失败:"+e.getMessage());
        }
    }

    @GetMapping("/attendance-status")
    public RetJson<List<Map<String,Object>>> attendanceStatus(){
        try{
            List<Map<String,Object>> list = jdbcTemplate.query("SELECT status, COUNT(*) AS cnt FROM emp_attendance_record WHERE attendance_date = CURRENT_DATE GROUP BY status", (rs,i)->{
                Map<String,Object> m=new HashMap<>();
                int status=rs.getInt("status");
                String name;
                switch(status){
                    case 1: name="正常";break;
                    case 2: name="迟到";break;
                    case 3: name="早退";break;
                    case 4: name="缺勤";break;
                    default: name="未知";break;
                }
                m.put("name", name);
                m.put("value", rs.getLong("cnt"));
                return m;
            });
            return RetJson.success(list);
        }catch(Exception e){
            e.printStackTrace();
            return RetJson.error("查询考勤状态统计失败:"+e.getMessage());
        }
    }

    @GetMapping("/attendance-dept-checkin")
    public RetJson<List<Map<String,Object>>> attendanceDeptCheckin(){
        try{
            String sql = "SELECT d.name AS name, COALESCE(a.cnt, 0) AS value " +
                    "FROM sys_dept d " +
                    "LEFT JOIN (" +
                    "    SELECT e.dept_id, COUNT(DISTINCT ar.user_id) AS cnt " +
                    "    FROM emp_attendance_record ar " +
                    "    JOIN emp e ON e.user_id = ar.user_id " +
                    "    WHERE ar.attendance_date = CURRENT_DATE " +
                    "      AND ar.check_in_time IS NOT NULL " +
                    "    GROUP BY e.dept_id" +
                    ") a ON a.dept_id = d.id " +
                    "WHERE d.status = 1 " +
                    "ORDER BY value DESC, d.id ASC";
            List<Map<String,Object>> list = jdbcTemplate.query(sql, (rs,i)->{
                Map<String,Object> m = new HashMap<>();
                m.put("name", rs.getString("name"));
                m.put("value", rs.getLong("value"));
                return m;
            });
            return RetJson.success(list);
        }catch(Exception e){
            e.printStackTrace();
            return RetJson.error("查询部门打卡人数失败:"+e.getMessage());
        }
    }

    @GetMapping("/add-leave-trend")
    public RetJson<Map<String,Object>> addLeaveTrend(){
        try{
            // 查询近6个月 yyyy-MM 格式
            String sqlNew = "SELECT DATE_FORMAT(entry_date,'%Y-%m') ym, COUNT(*) cnt FROM emp WHERE entry_date >= DATE_FORMAT(DATE_SUB(CURDATE(),INTERVAL 5 MONTH),'%Y-%m-01') GROUP BY ym ORDER BY ym";
            String sqlLeave = "SELECT DATE_FORMAT(create_time,'%Y-%m') ym, COUNT(*) cnt FROM emp_leave_record WHERE create_time >= DATE_FORMAT(DATE_SUB(CURDATE(),INTERVAL 5 MONTH),'%Y-%m-01') GROUP BY ym ORDER BY ym";
            Map<String,Long> newMap = new HashMap<>();
            jdbcTemplate.query(sqlNew,(rs)->{
                newMap.put(rs.getString("ym"), rs.getLong("cnt"));
            });
            Map<String,Long> leaveMap = new HashMap<>();
            jdbcTemplate.query(sqlLeave,(rs)->{
                leaveMap.put(rs.getString("ym"), rs.getLong("cnt"));
            });
            // 生成连续6个月数组
            java.time.LocalDate start = java.time.LocalDate.now().minusMonths(5).withDayOfMonth(1);
            java.util.List<String> months = new java.util.ArrayList<>();
            java.util.List<Long> addArr = new java.util.ArrayList<>();
            java.util.List<Long> leaveArr = new java.util.ArrayList<>();
            for(int i=0;i<6;i++){
                java.time.LocalDate d = start.plusMonths(i);
                String ym = d.getYear()+"-"+String.format("%02d",d.getMonthValue());
                months.add(ym);
                addArr.add(newMap.getOrDefault(ym,0L));
                leaveArr.add(leaveMap.getOrDefault(ym,0L));
            }
            Map<String,Object> data = new HashMap<>();
            data.put("months", months);
            data.put("add", addArr);
            data.put("leave", leaveArr);
            return RetJson.success(data);
        }catch(Exception e){
            e.printStackTrace();
            return RetJson.error("查询新增与请假人数趋势失败:"+e.getMessage());
        }
    }
}
