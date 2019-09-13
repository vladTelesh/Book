package com.effectivesoft.bookservice.rest.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class Validator {

    private List<String> extensions = new ArrayList<>(Arrays.asList("image/png", "image/jpg", "image/jpeg", "image/ief"));

    public boolean extensionValidator(String extension) {
        return extensions.contains(extension);
    }
}