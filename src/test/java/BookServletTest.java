package com.thesquad.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.thesquad.connection.DBConnection;
import com.thesquad.dao.BookAuthorDAO;
import com.thesquad.dao.BookDAO;
import com.thesquad.dao.BookTagDAO;
import com.thesquad.models.BookAuthorModel;
import com.thesquad.models.BookModel;
import com.thesquad.models.BookTagModel;
import com.thesquad.utils.Helpers;

class BookServletTest {

    private BookServlet bookServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private DBConnection connection;

    @BeforeEach
    void setUp() {
        bookServlet = new BookServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        connection = mock(DBConnection.class);
    }

    @Test
    void testDoGet_deleteBook() throws Exception {
        // Mock request parameters for delete action
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn("1");

        // Mock DAO
        BookDAO bookDao = mock(BookDAO.class);

        // Call the servlet
        bookServlet.doGet(request, response);

        // Verify the delete method was called
        verify(bookDao, times(1)).delete(1, connection);
        verify(response).sendRedirect(anyString());
    }

    @Test
    void testDoPost_createBookWithAuthorsAndTags() throws Exception {
        // Mock request parameters for creating a book
        when(request.getParameter("name")).thenReturn("Test Book");
        when(request.getParameter("isbn")).thenReturn("123456789");
        when(request.getParameter("numPages")).thenReturn("300");
        when(request.getParameter("editionNum")).thenReturn("2");
        when(request.getParameter("releaseYear")).thenReturn("2025");
        when(request.getParameter("category")).thenReturn("1");
        when(request.getParameter("status")).thenReturn("1");
        when(request.getParameter("location")).thenReturn("1");
        when(request.getParameter("publisher")).thenReturn("1");
        when(request.getParameterValues("authors")).thenReturn(new String[]{"10", "20"});
        when(request.getParameterValues("tags")).thenReturn(new String[]{"101", "102"});
        when(request.getParameter("edit")).thenReturn(null);

        // Mock DAO
        BookDAO bookDao = mock(BookDAO.class);
        BookAuthorDAO bookAuthorDao = mock(BookAuthorDAO.class);
        BookTagDAO bookTagDao = mock(BookTagDAO.class);

        // Mock Helpers
        Helpers helpers = mock(Helpers.class);
        when(Helpers.getIdOfLastRow("livro", connection)).thenReturn(5);

        // Call the servlet
        bookServlet.doPost(request, response);

        // Verify the book was created
        ArgumentCaptor<BookModel> bookCaptor = ArgumentCaptor.forClass(BookModel.class);
        verify(bookDao).create(bookCaptor.capture(), eq(connection));
        BookModel createdBook = bookCaptor.getValue();
        assertEquals("Test Book", createdBook.getName());
        assertEquals("123456789", createdBook.getIsbn());
        assertEquals(300, createdBook.getNumPages());
        assertEquals(2, createdBook.getEditionNum());
        assertEquals(2025, createdBook.getReleaseYear());

        // Verify authors were added
        ArgumentCaptor<BookAuthorModel> authorCaptor = ArgumentCaptor.forClass(BookAuthorModel.class);
        verify(bookAuthorDao, times(2)).create(authorCaptor.capture(), eq(connection));
        assertEquals(10, authorCaptor.getAllValues().get(0).getAuthorId());
        assertEquals(20, authorCaptor.getAllValues().get(1).getAuthorId());

        // Verify tags were added
        ArgumentCaptor<BookTagModel> tagCaptor = ArgumentCaptor.forClass(BookTagModel.class);
        verify(bookTagDao, times(2)).create(tagCaptor.capture(), eq(connection));
        assertEquals(101, tagCaptor.getAllValues().get(0).getTagId());
        assertEquals(102, tagCaptor.getAllValues().get(1).getTagId());

        // Verify redirection
        verify(response).sendRedirect(anyString());
    }

    @Test
    void testDoPost_editBook() throws Exception {
        // Mock request parameters for editing a book
        when(request.getParameter("edit")).thenReturn("true");
        when(request.getParameter("name")).thenReturn("Updated Book");
        when(request.getParameter("isbn")).thenReturn("987654321");
        when(request.getParameter("numPages")).thenReturn("250");
        when(request.getParameter("editionNum")).thenReturn("3");
        when(request.getParameter("releaseYear")).thenReturn("2024");
        when(request.getParameter("category")).thenReturn("2");
        when(request.getParameter("status")).thenReturn("2");
        when(request.getParameter("location")).thenReturn("2");
        when(request.getParameter("publisher")).thenReturn("2");

        // Mock DAO
        BookDAO bookDao = mock(BookDAO.class);

        // Call the servlet
        bookServlet.doPost(request, response);

        // Verify the book was updated
        ArgumentCaptor<BookModel> bookCaptor = ArgumentCaptor.forClass(BookModel.class);
        verify(bookDao).update(bookCaptor.capture(), eq(connection));
        BookModel updatedBook = bookCaptor.getValue();
        assertEquals("Updated Book", updatedBook.getName());
        assertEquals("987654321", updatedBook.getIsbn());
        assertEquals(250, updatedBook.getNumPages());
        assertEquals(3, updatedBook.getEditionNum());
        assertEquals(2024, updatedBook.getReleaseYear());

        // Verify redirection
        verify(response).sendRedirect(anyString());
    }
}
