package com.effectivesoft.bookservice.ui.config;

import com.effectivesoft.bookservice.common.dto.AuthorDto;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class AuthorComboBoxConverter implements Converter<AuthorDto, String> {
    @Override
    public Result<String> convertToModel(AuthorDto author, ValueContext context) {
        return Result.ok(author.getId());
    }

    @Override
    public AuthorDto convertToPresentation(String value, ValueContext context) {
        return new AuthorDto();
    }
}