package com.effectivesoft.bookservice.common.error;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class ApiError {
    @JsonProperty("status")
    private Integer statusCode;
    private String message;
    private List<Error> errors;

    public ApiError(Integer statusCode, String message, List<Error> errors) {
        super();
        this.statusCode = statusCode;
        this.message = message;
        this.errors = errors;
    }

    public ApiError(Integer statusCode, String message, Error error) {
        super();
        this.statusCode = statusCode;
        this.message = message;
        errors = Collections.singletonList(error);
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}
