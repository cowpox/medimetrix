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
import com.mmx.medimetrix.application.avaliacaoquestao.exceptions.AvaliacaoQuestaoNaoEncontradaException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;

@ControllerAdvice
@Order(2) // vem depois do de validação
public class ProblemDetailsHandler {

    // --- NOVO: 404 para AvaliacaoQuestao ---
    @ExceptionHandler(AvaliacaoQuestaoNaoEncontradaException.class)
    public ProblemDetail avaliacaoQuestaoNaoEncontrada(AvaliacaoQuestaoNaoEncontradaException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Resource not found");
        pd.setDetail(ex.getMessage()); // "Vínculo Avaliação↔Questão não encontrado"
        pd.setProperty("code", "AVALIACAO_QUESTAO_NAO_ENCONTRADA");
        return pd;
    }

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
    // --- AJUSTADO: DataIntegrityViolation com SQLState/constraint ---
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail dataIntegrity(DataIntegrityViolationException ex) {
        Throwable root = NestedExceptionUtils.getMostSpecificCause(ex);
        String sqlState = null;
        String rootMsg = (root != null ? root.getMessage() : "");
        try {
            sqlState = (String) root.getClass().getMethod("getSQLState").invoke(root);
        } catch (Exception ignore) { /* no-op */ }

        // normaliza para facilitar checagem
        String msgUp = (rootMsg != null ? rootMsg.toUpperCase() : "");

        if ("23505".equals(sqlState)) { // unique_violation
            ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
            pd.setTitle("Conflict");

            if (msgUp.contains("UQ_AQ_ORDEM_POR_AVAL")) {
                pd.setDetail("Já existe item com essa ORDEM para a mesma Avaliação.");
                pd.setProperty("code", "ORDEM_DUPLICADA_POR_AVALIACAO");
            } else {
                pd.setDetail("Violação de unicidade.");
                pd.setProperty("code", "UNIQUE_CONSTRAINT");
            }
            return pd;
        }

        if ("23503".equals(sqlState)) { // foreign_key_violation
            ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
            pd.setTitle("Conflict");
            pd.setDetail("Referência inválida: verifique chaves estrangeiras.");
            pd.setProperty("code", "FOREIGN_KEY_VIOLATION");
            return pd;
        }

        if ("23514".equals(sqlState)) { // check_violation
            ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
            pd.setTitle("Bad request");
            pd.setDetail("Violação de regra de negócio (CHECK constraint).");
            pd.setProperty("code", "CHECK_CONSTRAINT");
            return pd;
        }

        // fallback: integridade genérica como 400
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad request");
        pd.setDetail(rootMsg != null && !rootMsg.isBlank() ? rootMsg : ex.getMessage());
        pd.setProperty("code", "DATA_INTEGRITY");
        return pd;
    }

    // (opcional) parâmetro obrigatório ausente — útil para endpoints com ?all=true etc.
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail missingParam(MissingServletRequestParameterException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad request");
        pd.setDetail("Parâmetro obrigatório ausente: " + ex.getParameterName());
        pd.setProperty("code", "MISSING_REQUEST_PARAM");
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
