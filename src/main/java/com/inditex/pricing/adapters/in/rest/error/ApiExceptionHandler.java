package com.inditex.pricing.adapters.in.rest.error;

import com.inditex.pricing.adapters.in.rest.price.PriceNotFoundException;
import java.net.URI;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ProblemDetail problem =
                problem(
                        HttpStatus.BAD_REQUEST,
                        "HTTP_400",
                        "One or more request parameters are invalid.");
        var violations =
                exception.getParameterValidationResults().stream()
                        .flatMap(
                                result ->
                                        result.getResolvableErrors().stream()
                                                .map(
                                                        error ->
                                                                new ParameterViolation(
                                                                        result.getMethodParameter()
                                                                                .getParameterName(),
                                                                        error.getDefaultMessage())))
                        .toList();
        problem.setProperty("violations", violations);
        return handleExceptionInternal(exception, problem, headers, status, request);
    }

    @ExceptionHandler(PriceNotFoundException.class)
    ResponseEntity<Object> priceNotFound(PriceNotFoundException exception, WebRequest request) {
        ProblemDetail problem =
                problem(HttpStatus.NOT_FOUND, "PRICE_NOT_FOUND", exception.getMessage());
        return handleExceptionInternal(
                exception, problem, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ProblemDetail problem =
                problem(
                        HttpStatus.BAD_REQUEST,
                        "HTTP_400",
                        "One or more request parameters are invalid.");
        return handleExceptionInternal(
                exception, problem, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ProblemDetail problem =
                problem(
                        HttpStatus.BAD_REQUEST,
                        "HTTP_400",
                        "One or more request parameters are invalid.");
        return handleExceptionInternal(
                exception, problem, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ProblemDetail problem =
                problem(HttpStatus.METHOD_NOT_ALLOWED, "HTTP_405", exception.getMessage());
        if (exception.getSupportedHttpMethods() != null) {
            headers.setAllow(exception.getSupportedHttpMethods());
        }
        return handleExceptionInternal(
                exception, problem, headers, HttpStatus.METHOD_NOT_ALLOWED, request);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Object> unexpected(Exception exception, WebRequest request) {
        String errorId = UUID.randomUUID().toString();
        LOG.error("Unexpected pricing failure, errorId={}", errorId, exception);
        ProblemDetail problem =
                problem(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "INTERNAL_ERROR",
                        "An unexpected error occurred. Contact support with the errorId.");
        problem.setProperty("errorId", errorId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Error-Id", errorId);
        return handleExceptionInternal(
                exception, problem, headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception,
            Object body,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        Object response = body;
        if (response == null) {
            response =
                    createProblemDetail(
                            exception,
                            status,
                            "The request could not be processed.",
                            null,
                            null,
                            request);
        }
        return super.handleExceptionInternal(exception, response, headers, status, request);
    }

    private static ProblemDetail problem(HttpStatus status, String code, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(
                URI.create("urn:inditex:pricing:error:" + code.toLowerCase(java.util.Locale.ROOT)));
        problem.setProperty("code", code);
        return problem;
    }

    public record ParameterViolation(String field, String message) {}
}
