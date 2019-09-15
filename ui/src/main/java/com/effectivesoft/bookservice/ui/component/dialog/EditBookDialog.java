package com.effectivesoft.bookservice.ui.component.dialog;

import com.effectivesoft.bookservice.common.dto.AuthorDto;
import com.effectivesoft.bookservice.common.dto.BookDto;
import com.effectivesoft.bookservice.ui.client.AuthorRestClient;
import com.effectivesoft.bookservice.ui.client.BookRestClient;
import com.effectivesoft.bookservice.ui.component.AuthorComboBox;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class EditBookDialog extends Dialog {

    private static final Logger logger = LoggerFactory.getLogger(EditBookDialog.class);

    public EditBookDialog(BookDto book, BookRestClient bookRestClient, AuthorRestClient authorRestClient) {
        super();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.setClassName("dialog-main-layout");
        mainLayout.getElement().removeAttribute("theme");

        HorizontalLayout labelLayout = new HorizontalLayout();
        labelLayout.setWidthFull();
        labelLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        labelLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Label label = new Label();
        label.setText("Edit");
        label.setClassName("dialog-label");

        Binder<BookDto> binder = new Binder<>();

        TextField title = new TextField();
        title.setWidth("100%");
        title.setLabel("Title");
        if (book.getTitle() != null) {
            title.setValue(book.getTitle());
        }
        binder.forField(title)
                .withValidator(new StringLengthValidator("", 0, 45))
                .bind(BookDto::getTitle, BookDto::setTitle);

        AuthorComboBox authorComboBox = new AuthorComboBox(binder, authorRestClient);
        authorComboBox.setWidth("100%");
        authorComboBox.setLabel("Author");
        if (book.getAuthorName() != null) {
            AuthorDto author = new AuthorDto();
            author.setName(book.getAuthorName());
        }

        TextField additionalAuthors = new TextField();
        additionalAuthors.setWidth("100%");
        additionalAuthors.setLabel("Additional authors");
        if (book.getAdditionalAuthors() != null) {
            additionalAuthors.setValue(book.getAdditionalAuthors());
        }
        binder.forField(additionalAuthors)
                .bind(BookDto::getAdditionalAuthors, BookDto::setAdditionalAuthors);

        TextField isbn = new TextField();
        isbn.setLabel("ISBN");
        isbn.setWidth("50%");
        if (book.getISBN() != null) {
            isbn.setValue(book.getISBN());
        }
        binder.forField(isbn)
                .bind(BookDto::getISBN, BookDto::setISBN);

        TextField isbn13 = new TextField();
        isbn13.setLabel("ISBN13");
        isbn13.setWidth("50%");
        if (book.getISBN13() != null) {
            isbn13.setValue(book.getISBN13());
        }
        binder.forField(isbn13)
                .bind(BookDto::getISBN13, BookDto::setISBN13);


        TextField publisher = new TextField();
        publisher.setWidth("50%");
        publisher.setLabel("Publisher");
        if (book.getPublisher() != null) {
            publisher.setValue(book.getPublisher());
        }
        binder.forField(publisher)
                .bind(BookDto::getPublisher, BookDto::setPublisher);

        TextField binding = new TextField();
        binding.setWidth("50%");
        binding.setLabel("Publisher");
        if (book.getBinding() != null) {
            binding.setValue(book.getBinding());
        }
        binder.forField(binding)
                .bind(BookDto::getBinding, BookDto::setBinding);


        NumberField pagesNumber = new NumberField();
        pagesNumber.setWidth("50%");
        pagesNumber.setHasControls(true);
        pagesNumber.setLabel("Pages number");
        if (book.getPagesNumber() != null) {
            pagesNumber.setValue(book.getPagesNumber().doubleValue());
        }
        binder.forField(pagesNumber)
                .withConverter(Double::intValue,
                        Integer::doubleValue,
                        "Please enter a number")
                .withValidator(Objects::nonNull, "Pages number field can't be empty")
                .bind(BookDto::getPagesNumber, BookDto::setPagesNumber);

        NumberField publicationYear = new NumberField();
        publicationYear.setWidth("50%");
        publicationYear.setLabel("Publication year");
        publicationYear.setHasControls(true);
        if (book.getPublicationYear() != null) {
            publicationYear.setValue(book.getPublicationYear().doubleValue());
        }
        binder.forField(publicationYear)
                .withConverter(Double::intValue,
                        Integer::doubleValue,
                        "Please enter a number")
                .bind(BookDto::getPublicationYear, BookDto::setPublicationYear);

        NumberField originalPublicationYear = new NumberField();
        originalPublicationYear.setWidth("100%");
        originalPublicationYear.setLabel("Original publication year");
        originalPublicationYear.setHasControls(true);
        if (book.getOriginalPublicationYear() != null) {
            originalPublicationYear.setValue(book.getOriginalPublicationYear().doubleValue());
        }
        binder.forField(originalPublicationYear)
                .withConverter(Double::intValue,
                        Integer::doubleValue,
                        "Please enter a number")
                .bind(BookDto::getOriginalPublicationYear, BookDto::setOriginalPublicationYear);

        TextArea description = new TextArea();
        description.setWidth("100%");
        description.setHeight("150px");
        description.setLabel("description");
        if (book.getDescription() != null) {
            description.setValue(book.getDescription());
        }

        HorizontalLayout saveButtonLayout = new HorizontalLayout();
        saveButtonLayout.setWidthFull();
        saveButtonLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        saveButtonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Div saveButton = new Div();
        saveButton.add("Save");
        saveButton.setClassName("button");
        saveButton.addClickListener(onClick -> {
            try {
                binder.writeBeanIfValid(book);

                if (authorComboBox.getValue() != null) {
                    book.setAuthorId(authorComboBox.getValue().getId());
                }

                Optional<BookDto> updatedBook = bookRestClient.updateBook(book);
                if (updatedBook.isPresent()) {
                    super.close();
                    UI.getCurrent().getPage().reload();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        });

        saveButtonLayout.add(saveButton);


        HorizontalLayout firstLine = new HorizontalLayout();
        firstLine.setWidthFull();
        firstLine.add(title);

        HorizontalLayout secondLine = new HorizontalLayout();
        secondLine.setWidthFull();
        secondLine.add(authorComboBox);

        HorizontalLayout thirdLine = new HorizontalLayout();
        thirdLine.setWidthFull();
        thirdLine.add(additionalAuthors);

        HorizontalLayout fourthLine = new HorizontalLayout();
        fourthLine.setWidthFull();
        fourthLine.add(isbn, isbn13);

        HorizontalLayout fifthLine = new HorizontalLayout();
        fifthLine.setWidthFull();
        fifthLine.add(publisher, binding);

        HorizontalLayout sixthLine = new HorizontalLayout();
        sixthLine.setWidthFull();
        sixthLine.add(pagesNumber, publicationYear);

        HorizontalLayout seventhLine = new HorizontalLayout();
        seventhLine.setWidthFull();
        seventhLine.add(originalPublicationYear);


        mainLayout.add(label, new Hr(), firstLine, secondLine, thirdLine, fourthLine, fifthLine, sixthLine, seventhLine, description, saveButtonLayout);

        add(mainLayout);
    }
}
