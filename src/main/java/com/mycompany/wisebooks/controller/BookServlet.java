package com.mycompany.wisebooks.controller;

import com.mycompany.wisebooks.model.Book;
import com.mycompany.wisebooks.service.BookService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/books")
public class BookServlet extends HttpServlet {
    private final BookService bookService = new BookService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Obtener todos los libros
        List<Book> books = bookService.findAll();
        
        // Enviar los libros al JSP
        req.setAttribute("books", books);
        
        // Redirigir al JSP
        req.getRequestDispatcher("/books/index.jsp").forward(req, resp);
    }
}