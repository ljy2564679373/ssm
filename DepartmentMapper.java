package edu.java.eams.mapper;

import edu.java.eams.domain.Department;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DepartmentMapper {
    List<Department> findAll();
    Department findById(Long id);
    int insert(Department department);
    int update(Department department);
    int delete(Long id);
    List<Department> findByCondition(@Param("name") String name, @Param("start") int start, @Param("size") int size);
    int countByCondition(@Param("name") String name);
    int clearManager(@Param("managerUserId") Long managerUserId);
} 