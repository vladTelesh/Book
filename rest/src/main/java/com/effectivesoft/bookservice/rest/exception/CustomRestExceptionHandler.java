package com.effectivesoft.bookservice.rest.exception;


import com.effectivesoft.bookservice.common.error.ApiError;
import com.effectivesoft.bookservice.common.error.Error;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @Nullable HttpHeaders headers,
            @Nullable HttpStatus status,
            @Nullable WebRequest request) {
        List<Error> errors = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(new Error(error.getField(), error.getDefaultMessage()));
        }

        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(new Error(error.getObjectName(), error.getDefaultMessage()));
        }

        ApiError apiError = new ApiError(400, ex.getLocalizedMessage(), errors);

        return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, Objects.requireNonNull(request));
    }


    @Override
    @Nullable
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            @Nullable HttpHeaders headers,
            @Nullable HttpStatus status,
            @Nullable WebRequest request) {

        ApiError apiError = new ApiError(400, ex.getLocalizedMessage(),
                new Error("missingParameter", "Parameter '" + ex.getParameterName() + "' is missing"));

        return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, Objects.requireNonNull(request));
    }


    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(
            Exception ex,
            WebRequest request) {

        if (ex.getLocalizedMessage() == null && ex.getMessage() == null) {
            return new ResponseEntity<>(new ApiError(500, "Something went wrong, try again later",
                    new Error("Unexpected error occurred", "Something went wrong, try again later")),
                    new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ApiError apiError = new ApiError(500, ex.getLocalizedMessage(),
                new Error(ex.getLocalizedMessage(), ex.getMessage()));

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Override
    @Nullable
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            @Nullable HttpHeaders headers,
            @Nullable HttpStatus status,
            @Nullable WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are: ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));

        ApiError apiError = new ApiError(415,
                ex.getLocalizedMessage(),
                new Error(ex.getContentType() + " media type is not supported",
                        builder.substring(0, builder.length() - 2)));

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }


    @Override
    @Nullable
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            @Nullable HttpHeaders headers,
            @Nullable HttpStatus status,
            @Nullable WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append("Method '")
                .append(ex.getMethod())
                .append("' is not supported for this request. Supported methods are: ");

        Objects.requireNonNull(ex.getSupportedHttpMethods()).forEach(t -> builder.append(t).append(", "));

        ApiError apiError = new ApiError(405, builder.substring(0, builder.length() - 2),
                new Error(ex.getLocalizedMessage(), builder.substring(0, builder.length() - 2)));

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED);
    }


    @Override
    @Nullable
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            @Nullable HttpHeaders headers,
            @Nullable HttpStatus status,
            @Nullable WebRequest request) {
        String errorReason = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();

        ApiError apiError = new ApiError(404, ex.getLocalizedMessage(),
                new Error(errorReason, ex.getLocalizedMessage()));

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            WebRequest request) {
        String errorReason = "Request parameter '" + ex.getName() + "' should be of type " +
                Objects.requireNonNull(ex.getRequiredType()).getName();

        ApiError apiError = new ApiError(400, ex.getLocalizedMessage(),
                new Error(errorReason, ex.getMessage()));

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex,
            WebRequest request) {
        List<Error> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(new Error(violation.getRootBeanClass().getName() + " " +
                    violation.getPropertyPath() + ": " + violation.getMessage(), ex.getLocalizedMessage()));
        }

        ApiError apiError = new ApiError(400, ex.getLocalizedMessage(), errors);

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
