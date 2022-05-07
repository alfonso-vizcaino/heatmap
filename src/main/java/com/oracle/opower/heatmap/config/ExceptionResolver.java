package com.oracle.opower.heatmap.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestControllerAdvice
public class ExceptionResolver {

    public static final String SERVICE_NOT_FOUND = "Service not found";
    public static final String SERVICE_ERROR = "Service error";
    public static final String SERVICE_NOT_AVAILABLE = "Service not available";
    public static final String SERVICE_NOT_SUPPORTED = "Service not supported";
    public static final String UNKNOWN_ERROR = "Unknown error";
    public static final String ILLEGAL_ARGUMENT_ERROR = "Illegal Argument error";
    private static final Logger logger = Logger.getLogger(String.valueOf(ExceptionResolver.class));

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoHandlerFound(NoHandlerFoundException e, WebRequest request) {
        return getStringStringMap(SERVICE_NOT_FOUND, e);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleCommunicationError(HttpClientErrorException e, WebRequest request) {
        return getStringStringMap(SERVICE_ERROR, e);
    }

    @ExceptionHandler(ConnectException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleConnectionError(ConnectException e, WebRequest request) {
        return getStringStringMap(SERVICE_NOT_AVAILABLE, e);
    }

    @ExceptionHandler(UnknownHostException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleConnectionError(UnknownHostException e, WebRequest request) {
        return getStringStringMap(SERVICE_NOT_AVAILABLE, e);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Map<String, String> handleMethodNotAllowedError(HttpRequestMethodNotSupportedException e, WebRequest request) {
        return getStringStringMap(SERVICE_NOT_SUPPORTED, e);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleMethodNotAllowedError(IllegalArgumentException e, WebRequest request) {
        return getStringStringMap(ILLEGAL_ARGUMENT_ERROR, e);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleMethodNotAllowedError(Exception e, WebRequest request) {
        return getStringStringMap(UNKNOWN_ERROR, e);
    }

    private Map<String, String> getStringStringMap(String errorString, Exception e) {
        logger.log(Level.SEVERE, errorString, e);

        Map<String, String> response = new HashMap<>();
        response.put("error", errorString);
        response.put("message", e.getLocalizedMessage());
        return response;
    }
}
