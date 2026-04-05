package edu.java.eams.service;

import edu.java.eams.domain.Department;
import edu.java.eams.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public List<Department> getAllDepartments() {
        return departmentMapper.findAll();
    }

    @Override
    public Department getDepartmentById(Long id) {
        return departmentMapper.findById(id);
    }

    @Override
    public boolean addDepartment(Department department) {
        return departmentMapper.insert(department) > 0;
    }

    @Override
    public boolean updateDepartment(Department department) {
        return departmentMapper.update(department) > 0;
    }

    @Override
    public boolean deleteDepartment(Long id) {
        return departmentMapper.delete(id) > 0;
    }

    @Override
    public List<Department> getDepartmentsByCondition(String name, int start, int size) {
        return departmentMapper.findByCondition(name, start, size);
    }

    @Override
    public int countDepartmentsByCondition(String name) {
        return departmentMapper.countByCondition(name);
    }
} 
