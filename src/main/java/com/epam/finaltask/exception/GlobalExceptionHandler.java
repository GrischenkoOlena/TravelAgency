package com.epam.finaltask.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            EntityNotFoundException.class,
            NotEnoughMoneyException.class,
            UnableChangeStatusException.class
    })
    public String handleCustomException(Model model, Exception ex) {
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGlobalError(Model model, Exception ex, HttpStatus httpStatus) {
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("status", httpStatus.value());
        return "error";
    }

}
