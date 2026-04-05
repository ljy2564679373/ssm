package edu.java.eams.service;

import edu.java.eams.domain.User;

public interface LoginService {
    User login(String account, String password);
} 