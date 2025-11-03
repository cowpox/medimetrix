package com.mmx.medimetrix.web.advice;

import com.mmx.medimetrix.application.usuario.exceptions.EmailDuplicadoException;
import com.mmx.medimetrix.application.usuario.exceptions.EntidadeNaoEncontradaException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.mmx.medimetrix.application.medico.exceptions.CrmDuplicadoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.core.NestedExceptionUtils;

@ControllerAdvice
@Order(2) // vem depois do de validação
public class ProblemDetailsHandler {

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ProblemDetail notFound(EntidadeNaoEncontradaException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Resource not found");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(EmailDuplicadoException.class)
    public ProblemDetail conflict(EmailDuplicadoException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflict");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail badRequest(IllegalArgumentException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad request");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail conflictIllegalState(IllegalStateException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflict");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    // 409 – Regra de negócio: CRM duplicado
    @ExceptionHandler(CrmDuplicadoException.class)
    public ProblemDetail crmDuplicado(CrmDuplicadoException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflict");
        pd.setDetail(ex.getMessage()); // "Já existe médico com CRM 12345-PR"
        pd.setProperty("code", "CRM_DUPLICADO");
        return pd;
    }

    // 409 – Fallback: violação de unicidade vinda do banco (SQLState 23505)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail dataIntegrity(DataIntegrityViolationException ex) {
        Throwable root = NestedExceptionUtils.getMostSpecificCause(ex);
        String sqlState = null;
        try {
            sqlState = (String) root.getClass().getMethod("getSQLState").invoke(root);
        } catch (Exception ignore) { /* no-op */ }

        if ("23505".equals(sqlState)) { // unique_violation (PostgreSQL)
            ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
            pd.setTitle("Conflict");
            pd.setDetail("Violação de unicidade.");
            pd.setProperty("code", "UNIQUE_CONSTRAINT");
            return pd;
        }

        // Se não for unicidade, trate como 400 genérico de integridade
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad request");
        pd.setDetail(root != null ? root.getMessage() : ex.getMessage());
        pd.setProperty("code", "DATA_INTEGRITY");
        return pd;
    }

    // (Opcional) fallback genérico
    @ExceptionHandler(Exception.class)
    public ProblemDetail unexpected(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Unexpected error");
        pd.setDetail("An unexpected error occurred.");
        return pd;
    }
}
