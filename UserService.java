package edu.java.eams.service;

import edu.java.eams.domain.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    boolean addUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(Long id);
    List<User> getUsersByCondition(String name, int start, int size);
    int countUsersByCondition(String name);
} 