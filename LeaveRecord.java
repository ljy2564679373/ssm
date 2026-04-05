package edu.java.eams.domain;
import java.util.Date;
public class LeaveRecord {
    private Long id;
    private Long userId;
    private Date createTime;
    @org.springframework.format.annotation.DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm")
    private Date leaveStartTime;
    @org.springframework.format.annotation.DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm")
    private Date leaveEndTime;
    private String leaveReason;
    private String leaveTypeCode;
    private Long approveUserId;
    private Date approveTime;
    private String approveContent;
    private Integer status;
    private Date realEndTime;
    private String userName;
    // getter/setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getLeaveStartTime() { return leaveStartTime; }
    public void setLeaveStartTime(Date leaveStartTime) { this.leaveStartTime = leaveStartTime; }
    public Date getLeaveEndTime() { return leaveEndTime; }
    public void setLeaveEndTime(Date leaveEndTime) { this.leaveEndTime = leaveEndTime; }
    public String getLeaveReason() { return leaveReason; }
    public void setLeaveReason(String leaveReason) { this.leaveReason = leaveReason; }
    public String getLeaveTypeCode() { return leaveTypeCode; }
    public void setLeaveTypeCode(String leaveTypeCode) { this.leaveTypeCode = leaveTypeCode; }
    public Long getApproveUserId() { return approveUserId; }
    public void setApproveUserId(Long approveUserId) { this.approveUserId = approveUserId; }
    public Date getApproveTime() { return approveTime; }
    public void setApproveTime(Date approveTime) { this.approveTime = approveTime; }
    public String getApproveContent() { return approveContent; }
    public void setApproveContent(String approveContent) { this.approveContent = approveContent; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Date getRealEndTime() { return realEndTime; }
    public void setRealEndTime(Date realEndTime) { this.realEndTime = realEndTime; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
} 