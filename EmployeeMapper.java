package edu.java.eams.mapper;

import edu.java.eams.domain.Employee;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface EmployeeMapper {
    List<Employee> findAll();
    Employee findById(Long id);
    int insert(Employee employee);
    int update(Employee employee);
    int delete(Long id);
    List<Employee> findByCondition(@Param("name") String name, @Param("start") int start, @Param("size") int size);
    int countByCondition(@Param("name") String name);
    int clearUser(@org.apache.ibatis.annotations.Param("userId") Long userId);
    Employee findByUserId(@Param("userId") Long userId);
}
