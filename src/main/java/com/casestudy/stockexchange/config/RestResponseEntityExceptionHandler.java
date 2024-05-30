package com.casestudy.stockexchange.config;

import com.casestudy.stockexchange.exception.EntityAlreadyExistsException;
import com.casestudy.stockexchange.exception.ErrorMessage;
import com.casestudy.stockexchange.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {EntityNotFoundException.class, ResourceNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotFound(final RuntimeException ex, final WebRequest request) {
        return ErrorMessage.builder().error(ex.getMessage()).build();
    }

    @ExceptionHandler({EntityAlreadyExistsException.class})
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorMessage handleUnprocessableEntity(final RuntimeException ex, final WebRequest request) {
        return ErrorMessage.builder().error(ex.getMessage()).build();
    }

}
