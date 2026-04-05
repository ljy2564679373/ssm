package edu.java.eams.controller;
import edu.java.eams.domain.LeaveRecord;
import edu.java.eams.service.LeaveRecordService;
import edu.java.eams.comm.RetJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Controller
@RequestMapping("/leave")
public class LeaveRecordController {
    @Autowired
    private LeaveRecordService leaveRecordService;
    @GetMapping("/list")
    public String list(Model model) {
        // 统一走分页逻辑，避免不同入口看到不一致的数据
        return "redirect:/leave/page";
    }
    @GetMapping("/add")
    public String addForm() { return "leaveAdd"; }
    @PostMapping("/add")
    public String add(LeaveRecord leaveRecord, jakarta.servlet.http.HttpSession session) {
        edu.java.eams.domain.User user = (edu.java.eams.domain.User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }
        leaveRecord.setUserId(user.getId());
        leaveRecord.setCreateTime(new java.util.Date());
        leaveRecord.setStatus(1); // 待审核
        leaveRecordService.insert(leaveRecord);
        // 员工提交完申请后，跳转到分页页（普通用户即“我的请假”，管理员则看到所有记录）
        return "redirect:/leave/page";
    }
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("leave", leaveRecordService.getById(id));
        return "leaveEdit";
    }
    @PostMapping("/edit")
    public String edit(LeaveRecord leaveRecord) {
        leaveRecordService.update(leaveRecord);
        return "redirect:/leave/list";
    }
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        leaveRecordService.delete(id);
        return "redirect:/leave/list";
    }

    /**
     * AJAX 删除请假记录
     */
    @PostMapping("/deleteJson/{id}")
    @ResponseBody
    public RetJson<String> deleteJson(@PathVariable Long id) {
        try {
            leaveRecordService.delete(id);
            return RetJson.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return RetJson.error("删除失败：" + e.getMessage());
        }
    }
    @GetMapping("/page")
    public String page(@RequestParam(value = "employeeName", required = false) String employeeName,
                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                       jakarta.servlet.http.HttpSession session,
                       Model model) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int start = (pageNum - 1) * pageSize;
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        Long currentUserId = ((edu.java.eams.domain.User)session.getAttribute("currentUser")).getId();
        Long filterUserId = isAdmin!=null && isAdmin ? null : currentUserId;
        List<LeaveRecord> records = leaveRecordService.getByCondition(employeeName, filterUserId, start, pageSize);
        int total = leaveRecordService.countByCondition(employeeName, filterUserId);
        model.addAttribute("records", records);
        model.addAttribute("total", total);
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("employeeName", employeeName);
        return "leaveList";
    }
    @PostMapping("/approve/{id}")
    @ResponseBody
    public edu.java.eams.comm.RetJson<String> approve(@PathVariable("id") Long id, @RequestParam(required=false) String remark, jakarta.servlet.http.HttpSession session) {
        try {
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
            if (isAdmin == null || !isAdmin) {
                return edu.java.eams.comm.RetJson.error("无权限");
            }
            if(remark==null||remark.isEmpty()) remark="同意";
            edu.java.eams.domain.User admin = (edu.java.eams.domain.User) session.getAttribute("currentUser");
            edu.java.eams.domain.LeaveRecord rec = leaveRecordService.getById(id);
            if (rec == null) return edu.java.eams.comm.RetJson.error("记录不存在");
            leaveRecordService.updateStatus(id,2,admin.getId(),new java.util.Date(),remark);
            return edu.java.eams.comm.RetJson.success("已批准");
        }catch(Exception e){
            e.printStackTrace();
            return edu.java.eams.comm.RetJson.error("审批异常:"+e.getMessage());
        }
    }
    @PostMapping("/reject/{id}")
    @ResponseBody
    public edu.java.eams.comm.RetJson<String> reject(@PathVariable("id") Long id, @RequestParam(required=false) String remark, jakarta.servlet.http.HttpSession session) {
        try {
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
            if (isAdmin == null || !isAdmin) {
                return edu.java.eams.comm.RetJson.error("无权限");
            }
            if(remark==null||remark.isEmpty()) return edu.java.eams.comm.RetJson.error("请填写驳回原因");
            edu.java.eams.domain.User admin = (edu.java.eams.domain.User) session.getAttribute("currentUser");
            edu.java.eams.domain.LeaveRecord rec = leaveRecordService.getById(id);
            if (rec == null) return edu.java.eams.comm.RetJson.error("记录不存在");
            leaveRecordService.updateStatus(id,3,admin.getId(),new java.util.Date(),remark);
            return edu.java.eams.comm.RetJson.success("已驳回");
        }catch(Exception e){
            e.printStackTrace();
            return edu.java.eams.comm.RetJson.error("驳回异常:"+e.getMessage());
        }
    }
    @PostMapping("/status/{id}")
    @ResponseBody
    public edu.java.eams.comm.RetJson<String> updateStatus(@PathVariable Long id,
                                                           @RequestParam Integer status,
                                                           @RequestParam(required=false) String remark,
                                                           jakarta.servlet.http.HttpSession session){
        Boolean isAdmin=(Boolean)session.getAttribute("isAdmin");
        if(isAdmin==null||!isAdmin){return edu.java.eams.comm.RetJson.error("无权限");}
        if(status==null||status<0){return edu.java.eams.comm.RetJson.error("非法状态");}
        edu.java.eams.domain.User admin=(edu.java.eams.domain.User)session.getAttribute("currentUser");
        try{
            leaveRecordService.updateStatus(id,status,admin.getId(),new java.util.Date(),remark);
            return edu.java.eams.comm.RetJson.success("状态已更新");
        }catch(Exception e){e.printStackTrace();return edu.java.eams.comm.RetJson.error("更新失败"+e.getMessage());}
    }
    @InitBinder
    public void initBinder(org.springframework.web.bind.WebDataBinder binder){
        java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        sdf.setLenient(false);
        binder.registerCustomEditor(java.util.Date.class,new java.beans.PropertyEditorSupport(){
            @Override
            public void setAsText(String text){
                if(text==null||text.isEmpty()){
                    setValue(null);
                }else{
                    try{ setValue(sdf.parse(text)); }
                    catch(Exception e){ setValue(null);} }
            }
        });
    }
    public static class StatusDTO{
        public Long id; public Integer status; public String remark;
        public Long getId(){return id;} public void setId(Long id){this.id=id;}
        public Integer getStatus(){return status;} public void setStatus(Integer status){this.status=status;}
        public String getRemark(){return remark;} public void setRemark(String remark){this.remark=remark;}
    }
    @PostMapping("/batch-status")
    @ResponseBody
    public edu.java.eams.comm.RetJson<String> batchStatus(@RequestBody java.util.List<StatusDTO> list,
                                                          jakarta.servlet.http.HttpSession session){
        Boolean isAdmin=(Boolean)session.getAttribute("isAdmin");
        if(isAdmin==null||!isAdmin) return edu.java.eams.comm.RetJson.error("无权限");
        edu.java.eams.domain.User admin=(edu.java.eams.domain.User)session.getAttribute("currentUser");
        int success=0;
        java.util.Date now=new java.util.Date();
        for(StatusDTO dto:list){
            if(dto.id!=null&&dto.status!=null){
                success+=leaveRecordService.updateStatus(dto.id,dto.status,admin.getId(),now,dto.remark);
            }
        }
        return edu.java.eams.comm.RetJson.success("已保存 "+success+" 条");
    }
} 