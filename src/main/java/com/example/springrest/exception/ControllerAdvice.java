package com.example.springrest.exception;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(TransactionSystemException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ProblemDetail handleConstrainException(TransactionSystemException ex) {
        if (ex.getRootCause() instanceof ConstraintViolationException constraintViolationException) {

            Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
            List<String> errorList = new ArrayList<>(constraintViolations.size());
            for (final var constraint : constraintViolations) {
                String message = constraint.getMessage();
                String propertyPath = constraint.getPropertyPath().toString().split("\\.")[0]; //[2]
                errorList.add(propertyPath.substring(0, 1).toUpperCase() + propertyPath.substring(1)
                        + ": " +
                        message.substring(0, 1).toUpperCase() + message.substring(1));
            }

            return ProblemDetail
                    .forStatusAndDetail(HttpStatus.BAD_REQUEST, errorList.toString().replace("[", "").replace("]", ""));
        }
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ProblemDetail constraintViolationException(ConstraintViolationException ex, WebRequest request) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ProblemDetail illegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ProblemDetail resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getLocalizedMessage());
    }

    @ExceptionHandler(IllegalAccessException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ProblemDetail resourceNotFoundException(IllegalAccessException ex, WebRequest request) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getLocalizedMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ProblemDetail accessDeniedException(AccessDeniedException ex, WebRequest request) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getLocalizedMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail globalExceptionHandler(Exception ex, WebRequest request) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
    }
}