package com.mycompany.wisebooks.controller;

import com.mycompany.wisebooks.model.Role;
import com.mycompany.wisebooks.model.User;
import com.mycompany.wisebooks.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserService userService = new UserService();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        
        if (fullName == null || email == null || username == null || password == null ||
            fullName.trim().isEmpty() || email.trim().isEmpty() || 
            username.trim().isEmpty() || password.trim().isEmpty()) {
            req.setAttribute("error", "All fields are required");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }
        
        if (userService.existsByUsername(username.trim())) {
            req.setAttribute("error", "Username already exists");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }
        
        if (userService.existsByEmail(email.trim())) {
            req.setAttribute("error", "Email already registered");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }
        
        Role clientRole = new Role();
        clientRole.setId(3L);
        
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email.trim());
        user.setUsername(username.trim());
        user.setPassword(password.trim());
        user.setRole(clientRole);
        user.setActive(true);
        
        boolean registered = userService.register(user);
        
        if (registered) {
            req.setAttribute("success", "Registration successful! Please login.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        } else {
            req.setAttribute("error", "Registration failed. Please try again.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }
}