package edu.java.eams.mapper;

import edu.java.eams.domain.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    // 登录：根据账号和密码查找用户
    User findByAccountAndPassword(@Param("account") String account, @Param("password") String password);

    // 查询全部用户（测试用）
    java.util.List<User> findAll();

    User findById(Long id);
    int insert(User user);
    int update(User user);
    int delete(Long id);

    java.util.List<User> findByCondition(@Param("name") String name, @Param("start") int start, @Param("size") int size);
    int countByCondition(@Param("name") String name);
} 