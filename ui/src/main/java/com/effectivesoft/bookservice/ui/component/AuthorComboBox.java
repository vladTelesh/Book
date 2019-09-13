package com.effectivesoft.bookservice.ui.component;

import com.effectivesoft.bookservice.common.dto.AuthorDto;
import com.effectivesoft.bookservice.common.dto.BookDto;
import com.effectivesoft.bookservice.ui.client.AuthorRestClient;
import com.effectivesoft.bookservice.ui.config.AuthorComboBoxConverter;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AuthorComboBox extends ComboBox<AuthorDto> {

    private static final Logger logger = LoggerFactory.getLogger(AuthorComboBox.class);

    public AuthorComboBox(Binder<BookDto> binder, AuthorRestClient authorRestClient) {
        this.setDataProvider(
                (filter, offset, limit) -> {
                    try {
                        if (filter.length() < 3) {
                            return null;
                        } else {
                            return authorRestClient.readAuthorsByName(filter).stream();
                        }
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                        return null;
                    }
                },
                filter -> {
                    try {
                        if (filter.length() < 3) {
                            return 0;
                        } else {
                            return authorRestClient.readAuthorsCount(filter, false);
                        }
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                        return 0;
                    }
                }
        );
        this.setItemLabelGenerator(AuthorDto::getName);
    }
}
