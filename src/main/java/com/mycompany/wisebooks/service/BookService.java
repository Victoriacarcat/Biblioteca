package com.mycompany.wisebooks.service;

import com.mycompany.wisebooks.dao.BookDAO;
import com.mycompany.wisebooks.model.Book;
import java.util.List;

public class BookService {
    private final BookDAO bookDAO = new BookDAO();

    public List<Book> findAll() {
        List<Book> books = bookDAO.findAll();
        System.out.println("Libros encontrados: " + books.size());
        return books;
    }

    public Book findById(Long id) {
        return bookDAO.findById(id);
    }

    public void save(Book book) {
        bookDAO.save(book);
    }

    public void update(Book book) {
        bookDAO.update(book);
    }

    public void delete(Long id) {
        bookDAO.delete(id);
    }
}