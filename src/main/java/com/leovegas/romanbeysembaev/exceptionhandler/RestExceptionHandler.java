package com.leovegas.romanbeysembaev.exceptionhandler;

import com.leovegas.romanbeysembaev.exception.CustomBadRequestException;
import com.leovegas.romanbeysembaev.exception.CustomNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
@Slf4j
public class RestExceptionHandler {

    @Value("${rest.errors.display}")
    private boolean displayErrors;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(CustomNotFoundException e) {
        log.debug("404", e);
        return displayErrors ? e.getLocalizedMessage() : "";
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(CustomBadRequestException e) {
        log.debug("400", e);
        return displayErrors ? e.getLocalizedMessage() : "";
    }

    // This handler catches exceptions emitted by Spring, such as validation exceptions.
    // In a real project, they would have to be handled specifically
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleBadRequest(Throwable e) {
        log.error("500", e);
    }

}
