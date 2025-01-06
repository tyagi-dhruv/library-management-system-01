package com.thesquad.controllers;

import java.time.LocalDateTime;

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
import com.thesquad.dao.BookRequestDAO;
import com.thesquad.models.BookRequestModel;
import com.thesquad.utils.Helpers;

class BookRequestServletTest {

    private BookRequestServlet bookRequestServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private DBConnection connection;

    @BeforeEach
    void setUp() {
        bookRequestServlet = new BookRequestServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        connection = mock(DBConnection.class);
    }

    @Test
    void testDoGet_deleteBookRequest() throws Exception {
        // Mock request parameters for delete action
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn("1");

        // Mock DAO
        BookRequestDAO bookRequestDao = mock(BookRequestDAO.class);

        // Call the servlet
        bookRequestServlet.doGet(request, response);

        // Verify the delete method was called
        verify(bookRequestDao, times(1)).delete(1, connection);
        verify(response).sendRedirect(anyString());
    }

    @Test
    void testDoPost_createBookRequest() throws Exception {
        // Mock request parameters for create
        when(request.getParameter("book")).thenReturn("1");
        when(request.getParameter("reader")).thenReturn("2");
        when(request.getParameter("requestDate")).thenReturn("2025-01-05T10:00:00");
        when(request.getParameter("returnDate")).thenReturn("2025-01-10T10:00:00");
        when(request.getParameter("edit")).thenReturn(null);

        // Mock DAO and helpers
        BookRequestDAO bookRequestDao = mock(BookRequestDAO.class);
        LocalDateTime requestDate = LocalDateTime.of(2025, 1, 5, 10, 0);
        LocalDateTime returnDate = LocalDateTime.of(2025, 1, 10, 10, 0);
        Helpers helpers = mock(Helpers.class);

        when(Helpers.stringToDateTime("2025-01-05T10:00:00", true)).thenReturn(requestDate);
        when(Helpers.stringToDateTime("2025-01-10T10:00:00", true)).thenReturn(returnDate);

        // Call the servlet
        bookRequestServlet.doPost(request, response);

        // Verify the create method was called
        ArgumentCaptor<BookRequestModel> captor = ArgumentCaptor.forClass(BookRequestModel.class);
        verify(bookRequestDao).create(captor.capture(), eq(connection));
        BookRequestModel capturedRequest = captor.getValue();

        assertEquals(1, capturedRequest.getBookId());
        assertEquals(2, capturedRequest.getReaderId());
        assertEquals(requestDate, capturedRequest.getRequestDate());
        assertEquals(returnDate, capturedRequest.getReturnDate());

        // Verify redirection
        verify(response).sendRedirect(anyString());
    }

    @Test
    void testDoPost_editBookRequest() throws Exception {
        // Mock request parameters for edit
        when(request.getParameter("edit")).thenReturn("true");
        when(request.getParameter("book")).thenReturn("3");
        when(request.getParameter("reader")).thenReturn("4");
        when(request.getParameter("requestDate")).thenReturn("2025-01-06T10:00:00");
        when(request.getParameter("returnDate")).thenReturn("2025-01-15T10:00:00");

        // Mock DAO and helpers
        BookRequestDAO bookRequestDao = mock(BookRequestDAO.class);
        LocalDateTime requestDate = LocalDateTime.of(2025, 1, 6, 10, 0);
        LocalDateTime returnDate = LocalDateTime.of(2025, 1, 15, 10, 0);
        Helpers helpers = mock(Helpers.class);

        when(Helpers.stringToDateTime("2025-01-06T10:00:00", true)).thenReturn(requestDate);
        when(Helpers.stringToDateTime("2025-01-15T10:00:00", true)).thenReturn(returnDate);

        // Call the servlet
        bookRequestServlet.doPost(request, response);

        // Verify the update method was called
        ArgumentCaptor<BookRequestModel> captor = ArgumentCaptor.forClass(BookRequestModel.class);
        verify(bookRequestDao).update(captor.capture(), eq(connection));
        BookRequestModel capturedRequest = captor.getValue();

        assertEquals(3, capturedRequest.getBookId());
        assertEquals(4, capturedRequest.getReaderId());
        assertEquals(requestDate, capturedRequest.getRequestDate());
        assertEquals(returnDate, capturedRequest.getReturnDate());

        // Verify redirection
        verify(response).sendRedirect(anyString());
    }
}
