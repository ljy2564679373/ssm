package edu.java.eams.service.impl;

import edu.java.eams.domain.Employee;
import edu.java.eams.mapper.EmployeeMapper;
import edu.java.eams.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public List<Employee> getAllEmployees() {
        return employeeMapper.findAll();
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeMapper.findById(id);
    }

    @Override
    public boolean addEmployee(Employee employee) {
        return employeeMapper.insert(employee) > 0;
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        return employeeMapper.update(employee) > 0;
    }

    @Override
    public boolean deleteEmployee(Long id) {
        return employeeMapper.delete(id) > 0;
    }

    @Override
    public List<Employee> getEmployeesByCondition(String name, int pageNum, int pageSize) {
        int start = (pageNum - 1) * pageSize;
        if (start < 0) start = 0;
        return employeeMapper.findByCondition(name, start, pageSize);
    }

    @Override
    public int countEmployeesByCondition(String name) {
        return employeeMapper.countByCondition(name);
    }

    @Override
    public Employee getByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        return employeeMapper.findByUserId(userId);
    }
} 