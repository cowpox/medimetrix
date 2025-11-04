package com.mmx.medimetrix.web.advice;

import com.mmx.medimetrix.application.usuario.exceptions.EmailDuplicadoException;
import com.mmx.medimetrix.application.usuario.exceptions.EntidadeNaoEncontradaException;
import com.mmx.medimetrix.application.medico.exceptions.CrmDuplicadoException;
import com.mmx.medimetrix.application.avaliacaoquestao.exceptions.AvaliacaoQuestaoNaoEncontradaException;
import com.mmx.medimetrix.application.participacao.exceptions.ParticipacaoNaoEncontradaException;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(2) // vem depois do de validação
public class ProblemDetailsHandler {

    // 404 genérico (múltiplas entidades)
    @ExceptionHandler({
            EntidadeNaoEncontradaException.class,
            AvaliacaoQuestaoNaoEncontradaException.class,
            ParticipacaoNaoEncontradaException.class
    })
    public ProblemDetail notFound(RuntimeException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Resource not found");
        pd.setDetail(ex.getMessage());
        // Code específico por tipo (opcional)
        if (ex instanceof AvaliacaoQuestaoNaoEncontradaException) {
            pd.setProperty("code", "AVALIACAO_QUESTAO_NAO_ENCONTRADA");
        } else if (ex instanceof ParticipacaoNaoEncontradaException) {
            pd.setProperty("code", "PARTICIPACAO_NAO_ENCONTRADA");
        } else {
            pd.setProperty("code", "NOT_FOUND");
        }
        return pd;
    }

    // 409 – regra de negócio: CRM duplicado
    @ExceptionHandler(CrmDuplicadoException.class)
    public ProblemDetail crmDuplicado(CrmDuplicadoException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflict");
        pd.setDetail(ex.getMessage());
        pd.setProperty("code", "CRM_DUPLICADO");
        return pd;
    }

    // 409 – e-mail duplicado (se mantiver como negócio)
    @ExceptionHandler(EmailDuplicadoException.class)
    public ProblemDetail emailDuplicado(EmailDuplicadoException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflict");
        pd.setDetail(ex.getMessage());
        pd.setProperty("code", "EMAIL_DUPLICADO");
        return pd;
    }

    // 400 – argumentos inválidos
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail badRequest(IllegalArgumentException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad request");
        pd.setDetail(ex.getMessage());
        pd.setProperty("code", "BAD_REQUEST");
        return pd;
    }

    // 409/400 – violações de integridade do banco
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail dataIntegrity(DataIntegrityViolationException ex) {
        Throwable root = NestedExceptionUtils.getMostSpecificCause(ex);
        String sqlState = null;
        String rootMsg = (root != null ? root.getMessage() : "");
        try { sqlState = (String) root.getClass().getMethod("getSQLState").invoke(root); }
        catch (Exception ignore) { /* no-op */ }

        String msgUp = (rootMsg != null ? rootMsg.toUpperCase() : "");

        if ("23505".equals(sqlState)) { // unique_violation
            ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
            pd.setTitle("Conflict");
            // Exemplos: UQ_AQ_ORDEM_POR_AVAL ou UQ_PART_AVAL_MEDICO
            if (msgUp.contains("UQ_AQ_ORDEM_POR_AVAL")) {
                pd.setDetail("Já existe item com essa ORDEM para a mesma Avaliação.");
                pd.setProperty("code", "ORDEM_DUPLICADA_POR_AVALIACAO");
            } else if (msgUp.contains("UQ_PART_AVAL_MEDICO")) {
                pd.setDetail("Já existe participação para este médico nesta avaliação.");
                pd.setProperty("code", "PARTICIPACAO_DUPLICADA");
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

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad request");
        pd.setDetail(!rootMsg.isBlank() ? rootMsg : ex.getMessage());
        pd.setProperty("code", "DATA_INTEGRITY");
        return pd;
    }

    // 400 – parâmetro obrigatório ausente
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail missingParam(MissingServletRequestParameterException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad request");
        pd.setDetail("Parâmetro obrigatório ausente: " + ex.getParameterName());
        pd.setProperty("code", "MISSING_REQUEST_PARAM");
        return pd;
    }

    // 409 – estados inválidos de fluxo
    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail conflictIllegalState(IllegalStateException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflict");
        pd.setDetail(ex.getMessage());
        pd.setProperty("code", "ILLEGAL_STATE");
        return pd;
    }

    // 500 – fallback
    @ExceptionHandler(Exception.class)
    public ProblemDetail unexpected(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Unexpected error");
        pd.setDetail("An unexpected error occurred.");
        pd.setProperty("code", "UNEXPECTED");
        return pd;
    }
}
