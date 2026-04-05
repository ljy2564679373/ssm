package edu.java.eams.domain;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;


public class Employee {
    private Long id;
    private String name;
    private String code;
    private String email;
    private String mobile;
    private String idCard;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    private Integer sex;
    private String nationCode;
    private String politicalCode;
    private String nativePlace;
    private String graduateSchool;
    private String majorCode;
    private String highestEducationCode;
    private String highestDegreeCode;
    private String habitation;
    private Integer maritalStatus;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date entryDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date leaveDate;
    private Long deptId;
    private Long postLevelId;
    private Long userId;
    private Integer roleType;
    private Integer status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }
    public Integer getSex() { return sex; }
    public void setSex(Integer sex) { this.sex = sex; }
    public String getNationCode() { return nationCode; }
    public void setNationCode(String nationCode) { this.nationCode = nationCode; }
    public String getPoliticalCode() { return politicalCode; }
    public void setPoliticalCode(String politicalCode) { this.politicalCode = politicalCode; }
    public String getNativePlace() { return nativePlace; }
    public void setNativePlace(String nativePlace) { this.nativePlace = nativePlace; }
    public String getGraduateSchool() { return graduateSchool; }
    public void setGraduateSchool(String graduateSchool) { this.graduateSchool = graduateSchool; }
    public String getMajorCode() { return majorCode; }
    public void setMajorCode(String majorCode) { this.majorCode = majorCode; }
    public String getHighestEducationCode() { return highestEducationCode; }
    public void setHighestEducationCode(String highestEducationCode) { this.highestEducationCode = highestEducationCode; }
    public String getHighestDegreeCode() { return highestDegreeCode; }
    public void setHighestDegreeCode(String highestDegreeCode) { this.highestDegreeCode = highestDegreeCode; }
    public String getHabitation() { return habitation; }
    public void setHabitation(String habitation) { this.habitation = habitation; }
    public Integer getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(Integer maritalStatus) { this.maritalStatus = maritalStatus; }
    public Date getEntryDate() { return entryDate; }
    public void setEntryDate(Date entryDate) { this.entryDate = entryDate; }
    public Date getLeaveDate() { return leaveDate; }
    public void setLeaveDate(Date leaveDate) { this.leaveDate = leaveDate; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getPostLevelId() { return postLevelId; }
    public void setPostLevelId(Long postLevelId) { this.postLevelId = postLevelId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getRoleType() { return roleType; }
    public void setRoleType(Integer roleType) { this.roleType = roleType; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
