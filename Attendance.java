package edu.java.eams.domain;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
public class Attendance {
    private Long id;
    private Long userId;
    private String userName;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date attendanceDate;
    @DateTimeFormat(pattern="HH:mm")
    private java.sql.Time checkInTime;
    @DateTimeFormat(pattern="HH:mm")
    private java.sql.Time checkOutTime;
    private java.math.BigDecimal workHours;
    private Integer status;
    private String remark;
    private Date createTime;
    private Date updateTime;
    // getter/setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Date getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(Date attendanceDate) { this.attendanceDate = attendanceDate; }
    public java.sql.Time getCheckInTime() { return checkInTime; }
    public void setCheckInTime(java.sql.Time checkInTime) { this.checkInTime = checkInTime; }
    public java.sql.Time getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(java.sql.Time checkOutTime) { this.checkOutTime = checkOutTime; }
    public java.math.BigDecimal getWorkHours() { return workHours; }
    public void setWorkHours(java.math.BigDecimal workHours) { this.workHours = workHours; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
} 