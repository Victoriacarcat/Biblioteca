package com.mycompany.wisebooks.service;

import com.mycompany.wisebooks.dao.UserDAO;
import com.mycompany.wisebooks.model.Role;
import com.mycompany.wisebooks.model.User;
import java.util.List;

public class UserService {
    private UserDAO userDAO = new UserDAO();
    
    public User authenticate(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user != null && user.isActive() && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    
    public boolean register(User user) {
        if (userDAO.existsByUsername(user.getUsername())) {
            return false;
        }
        if (userDAO.existsByEmail(user.getEmail())) {
            return false;
        }
        userDAO.save(user);
        return true;
    }
    
    public List<User> findAll() {
        return userDAO.findAll();
    }
    
    public User findById(Long id) {
        return userDAO.findById(id);
    }
    
    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }
    
    public void update(User user) {
        userDAO.update(user);
    }
    
    public void delete(Long id) {
        userDAO.delete(id);
    }
    
    public boolean existsByUsername(String username) {
        return userDAO.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userDAO.existsByEmail(email);
    }
}