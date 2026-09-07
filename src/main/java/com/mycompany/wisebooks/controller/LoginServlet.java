package com.mycompany.wisebooks.controller;

import com.mycompany.wisebooks.model.User;
import com.mycompany.wisebooks.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserService userService = new UserService();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            req.setAttribute("error", "Username and password are required");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }
        
        User user = userService.authenticate(username.trim(), password.trim());
        
        if (user != null) {
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole().getName());
            session.setAttribute("userId", user.getId());
            session.setAttribute("fullName", user.getFullName());
            
            String role = user.getRole().getName();
            if ("ADMIN".equals(role)) {
                resp.sendRedirect(req.getContextPath() + "/admin/index.jsp");
            } else if ("EMPLOYEE".equals(role)) {
                resp.sendRedirect(req.getContextPath() + "/employee/index.jsp");
            } else {
                resp.sendRedirect(req.getContextPath() + "/index.jsp");
            }
        } else {
            req.setAttribute("error", "Invalid username or password");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}