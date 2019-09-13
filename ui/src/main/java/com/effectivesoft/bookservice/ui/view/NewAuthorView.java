package com.effectivesoft.bookservice.ui.view;

import com.effectivesoft.bookservice.common.dto.AuthorDto;
import com.effectivesoft.bookservice.ui.client.AuthorRestClient;
import com.effectivesoft.bookservice.ui.client.UserRestClient;
import com.effectivesoft.bookservice.ui.component.Header;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Optional;

@Route("new/author")
@PageTitle("New author • Book-service")
@StyleSheet(value = "styles/addViewStyle.css")
@JavaScript("")
public class NewAuthorView extends HorizontalLayout {

    private final UserRestClient userRestClient;
    private final AuthorRestClient authorRestClient;

    private static final Logger logger = LoggerFactory.getLogger(NewAuthorView.class);

    public NewAuthorView(@Autowired UserRestClient userRestClient,
                         @Autowired AuthorRestClient authorRestClient) throws IOException {
        this.userRestClient = userRestClient;
        this.authorRestClient = authorRestClient;
        this.load();
    }

    private void load() throws IOException {
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        Hr hr = new Hr();
        hr.setClassName("hr");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        mainLayout.setAlignItems(Alignment.CENTER);
        mainLayout.setWidth("70%");

        mainLayout.add(new Header(userRestClient), hr);

        this.setLabel(mainLayout);

        this.setInputFields(mainLayout);

        add(mainLayout);
    }

    private void setLabel(VerticalLayout mainLayout) {
        HorizontalLayout labelLayout = new HorizontalLayout();
        labelLayout.setWidthFull();
        labelLayout.setClassName("label-layout");
        labelLayout.setAlignItems(Alignment.CENTER);
        labelLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        Label label = new Label("New author");
        label.setClassName("label");

        labelLayout.add(label);

        Hr hr = new Hr();
        hr.setClassName("bottom-hr");

        mainLayout.add(labelLayout, hr);
    }

    private void setInputFields(VerticalLayout mainLayout) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setClassName("fields-layout");
        verticalLayout.getElement().removeAttribute("style");
        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout nameLayout = new HorizontalLayout();
        nameLayout.setAlignItems(Alignment.CENTER);
        nameLayout.setWidthFull();

        Label nameLabel = new Label("Name:");
        nameLabel.setClassName("field-label");
        nameLabel.setWidth("10%");

        TextField nameField = new TextField();
        nameField.setLabel("Name");
        nameField.setWidth("80%");


        nameLayout.add(nameLabel, nameField);


        HorizontalLayout bornLayout = new HorizontalLayout();
        bornLayout.setAlignItems(Alignment.CENTER);
        bornLayout.setWidthFull();

        Label bornLabel = new Label("Born:");
        bornLabel.setWidth("10%");
        bornLabel.setClassName("field-label");

        DatePicker dateOfBirth = new DatePicker();
        dateOfBirth.setLabel("Date of birth");
        dateOfBirth.setWidth("36%");

        TextField placeOfBirth = new TextField();
        placeOfBirth.setLabel("Place of birth");
        placeOfBirth.setWidth("36%");

        bornLayout.add(bornLabel, dateOfBirth, placeOfBirth);


        HorizontalLayout diedLayout = new HorizontalLayout();
        diedLayout.setAlignItems(Alignment.CENTER);
        diedLayout.setWidthFull();

        Label diedLabel = new Label("Died:");
        diedLabel.setClassName("field-label");
        diedLabel.setWidth("10%");

        DatePicker dateOfDeath = new DatePicker();
        dateOfDeath.setLabel("Date of death");
        dateOfBirth.setClassName("date-of-birth-input");

        diedLayout.add(diedLabel, dateOfDeath);


        HorizontalLayout genreLayout = new HorizontalLayout();
        genreLayout.setAlignItems(Alignment.CENTER);
        genreLayout.setWidthFull();

        Label genreLabel = new Label("Genre:");
        genreLabel.setWidth("10%");
        genreLabel.setClassName("field-label");

        TextField genre = new TextField();
        genre.setLabel("Genre");

        genreLayout.add(genreLabel, genre);


        HorizontalLayout photoLayout = new HorizontalLayout();
        photoLayout.setAlignItems(Alignment.CENTER);
        photoLayout.setWidthFull();

        Label uploadLabel = new Label("Photo:");
        uploadLabel.setWidth("10%");
        uploadLabel.setClassName("photo-field-label");

        MemoryBuffer buffer = new MemoryBuffer();

        Div uploadButton = new Div();
        uploadButton.add("Upload file");
        uploadButton.setClassName("upload-button");

        Upload upload = new Upload(buffer);
        upload.setUploadButton(uploadButton);
        upload.setMaxFiles(1);
        upload.setAcceptedFileTypes("image/*");

        photoLayout.add(uploadLabel, upload);


        VerticalLayout biographyLayout = new VerticalLayout();
        biographyLayout.setWidthFull();
        biographyLayout.getElement().removeAttribute("theme");
        biographyLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        Label biographyLabel = new Label("Biography:");
        biographyLabel.setWidth("10%");
        biographyLabel.setClassName("biography-label");

        TextArea biography = new TextArea();
        biography.setWidth("100%");

        biographyLayout.add(biographyLabel, biography);


        HorizontalLayout createButtonLayout = new HorizontalLayout();
        createButtonLayout.setAlignItems(Alignment.CENTER);
        createButtonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        createButtonLayout.setWidthFull();

        Div button = new Div();
        button.add("Create");
        button.setClassName("create-button");
        button.addClickListener(onClick -> {
            if (!(nameField.getValue().length() == 0)) {
                nameField.setInvalid(false);

                AuthorDto author = new AuthorDto();

                author.setName(nameField.getValue().trim());
                author.setDateOfBirth(dateOfBirth.getValue());
                author.setPlaceOfBirth(placeOfBirth.getValue());
                author.setDateOfDeath(dateOfDeath.getValue());
                author.setGenre(genre.getValue());
                author.setBiography(biography.getValue());

                try {
                    Optional<AuthorDto> optionalAuthor = authorRestClient.createAuthor(author);
                    if(optionalAuthor.isPresent()){
                        if(buffer.getFileData() != null && !upload.isUploading()) {
                            if(!authorRestClient.updateAuthorPhoto(optionalAuthor.get().getId(), buffer.getFileName(),
                                    buffer.getFileData().getMimeType(),
                                    buffer.getInputStream().readAllBytes())){
                                logger.error("Error to create image!");
                            }
                        }
                        UI.getCurrent().navigate("author/" + optionalAuthor.get().getId());
                    } else {
                        logger.error("Error to create author!");
                        //todo; redirect to error page
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            } else {
                nameField.setInvalid(false);
            }
        });

        createButtonLayout.add(button);

        verticalLayout.add(nameLayout, bornLayout, diedLayout, genreLayout, photoLayout, biographyLayout, createButtonLayout);

        mainLayout.add(verticalLayout);
    }
}
