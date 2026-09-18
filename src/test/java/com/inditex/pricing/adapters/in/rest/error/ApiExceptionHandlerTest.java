package com.inditex.pricing.adapters.in.rest.error;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

class ApiExceptionHandlerTest {

    @Test
    void unsupportedMethodWithoutAdvertisedMethodsStillReturnsProblem() {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var exception = new HttpRequestMethodNotSupportedException("POST", null);

        ResponseEntity<Object> result =
                new Probe()
                        .invoke(
                                exception,
                                new HttpHeaders(),
                                new ServletWebRequest(request, response));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(result.getBody()).isInstanceOf(org.springframework.http.ProblemDetail.class);
    }

    @Test
    void advertisesSupportedMethodsForUnsupportedRequest() {
        WebRequest request =
                new ServletWebRequest(new MockHttpServletRequest(), new MockHttpServletResponse());
        var exception = new HttpRequestMethodNotSupportedException("POST", java.util.Set.of("GET"));

        ResponseEntity<Object> result = new Probe().invoke(exception, new HttpHeaders(), request);

        assertThat(result.getHeaders().getAllow())
                .containsExactly(org.springframework.http.HttpMethod.GET);
    }

    @Test
    void createsDefaultProblemWhenNoBodyIsProvided() {
        WebRequest request =
                new ServletWebRequest(new MockHttpServletRequest(), new MockHttpServletResponse());

        ResponseEntity<Object> result =
                new Probe()
                        .invokeInternal(
                                new IllegalStateException(), null, new HttpHeaders(), request);

        assertThat(result.getBody()).isInstanceOf(org.springframework.http.ProblemDetail.class);
    }

    @Test
    void preservesNonProblemBodyWhenProvided() {
        WebRequest request =
                new ServletWebRequest(new MockHttpServletRequest(), new MockHttpServletResponse());

        ResponseEntity<Object> result =
                new Probe()
                        .invokeInternal(
                                new IllegalStateException(), "body", new HttpHeaders(), request);

        assertThat(result.getBody()).isEqualTo("body");
    }

    private static final class Probe extends ApiExceptionHandler {
        ResponseEntity<Object> invoke(
                HttpRequestMethodNotSupportedException exception,
                HttpHeaders headers,
                WebRequest request) {
            return handleHttpRequestMethodNotSupported(
                    exception, headers, HttpStatus.METHOD_NOT_ALLOWED, request);
        }

        ResponseEntity<Object> invokeInternal(
                Exception exception, Object body, HttpHeaders headers, WebRequest request) {
            return handleExceptionInternal(
                    exception, body, headers, HttpStatus.BAD_REQUEST, request);
        }
    }
}
