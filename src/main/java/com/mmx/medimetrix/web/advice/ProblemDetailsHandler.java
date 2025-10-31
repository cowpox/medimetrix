package com.mmx.medimetrix.web.advice;

import com.mmx.medimetrix.application.usuario.exceptions.EmailDuplicadoException;
import com.mmx.medimetrix.application.usuario.exceptions.EntidadeNaoEncontradaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class ProblemDetailsHandler {

    @ExceptionHandler(EmailDuplicadoException.class)
    public ProblemDetail handleEmailDuplicado(EmailDuplicadoException ex){
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Conflito de dados");
        pd.setType(URI.create("https://mmx.docs/errors/email-duplicado"));
        return pd;
    }

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ProblemDetail handleNotFound(EntidadeNaoEncontradaException ex){
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Recurso não encontrado");
        pd.setType(URI.create("https://mmx.docs/errors/nao-encontrado"));
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex){
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        pd.setTitle("Falha de validação");
        pd.setDetail("Um ou mais campos são inválidos.");
        pd.setType(URI.create("https://mmx.docs/errors/validacao"));
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex){
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado");
        pd.setTitle("Erro interno");
        pd.setType(URI.create("https://mmx.docs/errors/interno"));
        return pd;
    }
}
