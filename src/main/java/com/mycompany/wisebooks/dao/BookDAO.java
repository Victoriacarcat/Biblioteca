package com.mycompany.wisebooks.dao;

import com.mycompany.wisebooks.model.Book;

public class BookDAO extends GenericDAO<Book> {
    public BookDAO() {
        super(Book.class);
    }
}