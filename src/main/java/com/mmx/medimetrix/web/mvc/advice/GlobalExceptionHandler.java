package com.mmx.medimetrix.web.mvc.advice;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handle(Model model, Exception ex) {
        model.addAttribute("pageTitle", "Erro");
        model.addAttribute("breadcrumb", "Erro");
        model.addAttribute("flash_error", ex.getMessage());
        return "error/500";
    }
}