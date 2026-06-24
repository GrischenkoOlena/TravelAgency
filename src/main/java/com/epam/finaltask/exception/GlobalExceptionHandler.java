package com.epam.finaltask.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


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
    public String handleGlobalError(Model model, Exception ex, HttpServletResponse response) {
        model.addAttribute("error", ex.getMessage());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("status", response.getStatus());
        return "error";
    }

}
