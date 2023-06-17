package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExceptionTest {
    private final ExceptionHandlers handler = new ExceptionHandlers();

    @Test
    public void notFoundExceptionTest() {
        NotFoundException e = new NotFoundException("not found");
        ErrorResponse errorResponse = handler.notFoundException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    public void validationExceptionTest() {
        ValidationException e = new ValidationException("validation error");
        ErrorResponse errorResponse = handler.validationException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    public void wrongUserExceptionTest() {
        WrongUserException e = new WrongUserException("wrong user");
        ErrorResponse errorResponse = handler.wrongUserException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    public void emailConflictExceptionTest() {
        EmailConflictException e = new EmailConflictException("email error");
        ErrorResponse errorResponse = handler.emailConflictException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    public void badRequestExceptionTest() {
        BadRequestException e = new BadRequestException("bad request");
        ErrorResponse errorResponse = handler.badRequestException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    public void runtimeExceptionTest() {
        RuntimeException e = new RuntimeException("other errors");
        ErrorResponse errorResponse = handler.runtimeException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }
}
