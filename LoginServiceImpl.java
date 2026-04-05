package edu.java.eams.service.impl;

import edu.java.eams.domain.User;
import edu.java.eams.mapper.UserMapper;
import edu.java.eams.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 登录业务实现
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String account, String password) {
        // 直接通过Mapper查询
        return userMapper.findByAccountAndPassword(account, password);
    }
} 