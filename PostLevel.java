package edu.java.eams.domain;

import java.math.BigDecimal;

public class PostLevel {
    private Long id;
    private String name;
    private Integer level;
    private BigDecimal salary;
    private Integer sortCode;
    private Integer status;
    private Integer employeeCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public Integer getSortCode() { return sortCode; }
    public void setSortCode(Integer sortCode) { this.sortCode = sortCode; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(Integer employeeCount) { this.employeeCount = employeeCount; }
} 
