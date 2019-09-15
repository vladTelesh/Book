package com.effectivesoft.bookservice.ui.view;

import com.effectivesoft.bookservice.common.dto.BookDto;
import com.effectivesoft.bookservice.ui.client.AuthorRestClient;
import com.effectivesoft.bookservice.ui.client.BookRestClient;
import com.effectivesoft.bookservice.ui.client.UserRestClient;
import com.effectivesoft.bookservice.ui.component.AuthorComboBox;
import com.effectivesoft.bookservice.ui.component.Header;
import com.effectivesoft.bookservice.ui.config.AuthorComboBoxConverter;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Route("new/book")
@PageTitle("New book • Book-service")
@StyleSheet(value = "styles/addViewStyle.css")
public class NewBookView extends HorizontalLayout {
    private final UserRestClient userRestClient;
    private final BookRestClient bookRestClient;
    private final AuthorRestClient authorRestClient;

    private static final String TITLE_FIELD = "Title";
    private static final String ADDITIONAL_AUTHORS_FIELD = "Additional authors";
    private static final String ISBN_FIELD = "ISBN";
    private static final String ISBN13_FIELD = "ISBN13";
    private static final String PUBLISHER_FIELD = "Publisher";
    private static final String BINDING_FIELD = "Binding";
    private static final String PAGES_NUMBER_FIELD = "Pages number";
    private static final String PUBLICATION_YEAR_FIELD = "Publication year";
    private static final String ORIGINAL_PUBLICATION_YEAR_FIELD = "Original publication year";


    private static final Logger logger = LoggerFactory.getLogger(NewAuthorView.class);

    public NewBookView(@Autowired UserRestClient userRestClient,
                       @Autowired BookRestClient bookRestClient,
                       @Autowired AuthorRestClient authorRestClient) throws IOException {
        this.userRestClient = userRestClient;
        this.bookRestClient = bookRestClient;
        this.authorRestClient = authorRestClient;
        this.load();
    }

    private void load() throws IOException {
        getElement().removeAttribute("style");
        setWidthFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setClassName("main-layout");
        mainLayout.setAlignItems(Alignment.CENTER);
        mainLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        mainLayout.setWidth("70%");

        Hr hr = new Hr();
        hr.setClassName("hr");

        mainLayout.add(new Header(userRestClient), hr);

        this.setLabel(mainLayout);

        this.setInputFields(mainLayout);

        add(mainLayout);
    }

    private void setLabel(VerticalLayout mainLayout) {
        HorizontalLayout labelLayout = new HorizontalLayout();
        labelLayout.setWidthFull();
        labelLayout.setClassName("label-layout");
        labelLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        labelLayout.setAlignItems(Alignment.CENTER);

        Label label = new Label("New book");
        label.setClassName("label");

        labelLayout.add(label);

        Hr hr = new Hr();
        hr.setClassName("bottom-hr");

        mainLayout.add(labelLayout, hr);
    }

    private void setInputFields(VerticalLayout mainLayout) {
        Binder<BookDto> binder = new Binder<>(BookDto.class);
        BookDto book = new BookDto();

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setClassName("fields-layout");
        verticalLayout.getElement().removeAttribute("style");
        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        this.setTextFieldLayout(verticalLayout, "String", TITLE_FIELD, binder);

        this.setAuthorComboBoxLayout(verticalLayout, binder);

        this.setTextFieldLayout(verticalLayout, "String", ADDITIONAL_AUTHORS_FIELD, binder);

        this.setTextFieldLayout(verticalLayout, "String", ISBN_FIELD, binder);

        this.setTextFieldLayout(verticalLayout, "String", ISBN13_FIELD, binder);

        this.setTextFieldLayout(verticalLayout, "String", PUBLISHER_FIELD, binder);

        this.setTextFieldLayout(verticalLayout, "String", BINDING_FIELD, binder);

        this.setTextFieldLayout(verticalLayout, "Number", PAGES_NUMBER_FIELD, binder);

        this.setTextFieldLayout(verticalLayout, "Number", PUBLICATION_YEAR_FIELD, binder);

        this.setTextFieldLayout(verticalLayout, "Number", ORIGINAL_PUBLICATION_YEAR_FIELD, binder);

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        this.setUploadLayout(verticalLayout, upload, buffer);

        VerticalLayout descriptionLayout = new VerticalLayout();
        descriptionLayout.setWidthFull();
        descriptionLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        descriptionLayout.getElement().removeAttribute("theme");

        Label descriptionLabel = new Label("Description:");
        descriptionLabel.setWidth("10%");
        descriptionLabel.setClassName("description-label");

        TextArea description = new TextArea();
        binder.forField(description)
                .bind(BookDto::getDescription, BookDto::setDescription);
        description.setWidth("100%");


        descriptionLayout.add(descriptionLabel, description);


        HorizontalLayout createButtonLayout = new HorizontalLayout();
        createButtonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        createButtonLayout.setAlignItems(Alignment.CENTER);
        createButtonLayout.setWidthFull();

        Div button = new Div();
        button.add("Create");
        button.setClassName("create-button");
        button.addClickListener(onClick -> {
            if (binder.validate().isOk()) {
                binder.writeBeanIfValid(book);

                try {
                    Optional<BookDto> optionalBook = bookRestClient.createBook(book);
                    optionalBook.ifPresent(bookDto -> {
                        if (buffer.getFileData() != null && !upload.isUploading()) {
                            try {
                                if (!bookRestClient.updateBookImage(optionalBook.get().getId(), buffer.getFileName(),
                                        buffer.getFileData().getMimeType(),
                                        buffer.getInputStream().readAllBytes())) {
                                    logger.error("Error to create image!");
                                }
                            } catch (IOException e) {
                                logger.error(e.getMessage());
                            }
                        }
                        UI.getCurrent().navigate("book/" + optionalBook.get().getId());
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        createButtonLayout.add(button);

        verticalLayout.add(descriptionLayout, createButtonLayout);

        mainLayout.add(verticalLayout);
    }

    private void setTextFieldLayout(VerticalLayout verticalLayout, String type, String fieldType, Binder<BookDto> binder) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setAlignItems(Alignment.CENTER);

        Label label = new Label(fieldType + ":");
        label.setWidth("250px");
        label.setClassName("add-book-field-label");

        switch (type) {
            case "String":
                TextField stringField = new TextField();
                switch (fieldType) {
                    case TITLE_FIELD:
                        binder.forField(stringField)
                                .withValidator(Objects::nonNull, "Title can't be empty")
                                .bind(BookDto::getTitle, BookDto::setTitle);
                        break;
                    case ADDITIONAL_AUTHORS_FIELD:
                        binder.forField(stringField)
                                .bind(BookDto::getAdditionalAuthors, BookDto::setAdditionalAuthors);
                        break;
                    case ISBN_FIELD:
                        binder.forField(stringField)
                                .withValidator(new StringLengthValidator(
                                        "ISBN must have 10 characters", 10, 10))
                                .bind(BookDto::getISBN, BookDto::setISBN);
                        break;
                    case ISBN13_FIELD:
                        binder.forField(stringField)
                                .withValidator(new StringLengthValidator(
                                        "ISBN13 must have 13 characters", 13, 13))
                                .bind(BookDto::getISBN13, BookDto::setISBN13);
                        break;
                    case PUBLISHER_FIELD:
                        binder.forField(stringField)
                                .bind(BookDto::getPublisher, BookDto::setPublisher);
                        break;
                    case BINDING_FIELD:
                        binder.forField(stringField)
                                .bind(BookDto::getBinding, BookDto::setBinding);
                        break;
                }


                horizontalLayout.add(label, stringField);
                verticalLayout.add(horizontalLayout);
                break;
            case "Number":
                NumberField numberField = new NumberField();
                switch (fieldType) {
                    case PAGES_NUMBER_FIELD:
                        binder.forField(numberField)
                                .withConverter(Double::intValue,
                                        Integer::doubleValue,
                                        "Please enter a number")
                                .withValidator(Objects::nonNull, "Pages number field can't be empty")
                                .bind(BookDto::getPagesNumber, BookDto::setPagesNumber);
                        break;
                    case PUBLICATION_YEAR_FIELD:
                        binder.forField(numberField)
                                .withConverter(Double::intValue,
                                        Integer::doubleValue,
                                        "Please enter a number")
                                .bind(BookDto::getPublicationYear, BookDto::setPublicationYear);
                        break;
                    case ORIGINAL_PUBLICATION_YEAR_FIELD:
                        binder.forField(numberField)
                                .withConverter(Double::intValue,
                                        Integer::doubleValue,
                                        "Please enter a number")
                                .bind(BookDto::getOriginalPublicationYear, BookDto::setOriginalPublicationYear);
                        break;
                }
                numberField.setHasControls(true);
                numberField.setWidth("50%");
                horizontalLayout.add(label, numberField);
                verticalLayout.add(horizontalLayout);
                break;
        }
    }

    private void setAuthorComboBoxLayout(VerticalLayout verticalLayout, Binder<BookDto> binder) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setAlignItems(Alignment.CENTER);

        Label label = new Label("Author" + ":");
        label.setWidth("250px");
        label.setClassName("add-book-field-label");

        AuthorComboBox authorComboBox = new AuthorComboBox(binder, authorRestClient);
        binder.forField(authorComboBox)
                .withValidator((value, message) -> {
                    if (value != null) {
                        return ValidationResult.ok();
                    } else {
                        return ValidationResult.error("Author field can't be empty!");
                    }
                })
                .withConverter(new AuthorComboBoxConverter())
                .bind(BookDto::getAuthorId, BookDto::setAuthorId);

        horizontalLayout.add(label, authorComboBox);
        verticalLayout.add(horizontalLayout);
    }

    private void setUploadLayout(VerticalLayout verticalLayout, Upload upload, MemoryBuffer buffer) {
        HorizontalLayout imageLayout = new HorizontalLayout();
        imageLayout.setAlignItems(Alignment.CENTER);
        imageLayout.setWidthFull();

        Label uploadLabel = new Label("Photo:");
        uploadLabel.setClassName("photo-field-label");
        uploadLabel.setWidth("10%");

        Div uploadButton = new Div();
        uploadButton.setClassName("upload-button");
        uploadButton.add("Upload file");

        upload.setWidthFull();
        upload.setUploadButton(uploadButton);
        upload.setMaxFiles(1);
        upload.setAcceptedFileTypes("image/*");

        imageLayout.add(uploadLabel, upload);

        verticalLayout.add(imageLayout);
    }
}
