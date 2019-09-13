package com.effectivesoft.bookservice.ui.component.dialog;


import com.effectivesoft.bookservice.common.dto.AuthorDto;
import com.effectivesoft.bookservice.ui.client.AuthorRestClient;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class EditAuthorDialog extends Dialog {

    private static final Logger logger = LoggerFactory.getLogger(EditAuthorDialog.class);

    public EditAuthorDialog(AuthorDto author, AuthorRestClient authorRestClient) {
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

        labelLayout.add(label);

        Binder<AuthorDto> binder = new Binder<>();

        TextField name = new TextField();
        name.setWidth("100%");
        name.setLabel("Name");
        if(author.getName() != null) {
            name.setValue(author.getName());
        }
        binder.forField(name)
                .bind(AuthorDto::getName, AuthorDto::setName);


        DatePicker dateOfBirth = new DatePicker();
        dateOfBirth.setWidth("50%");
        dateOfBirth.setLabel("Date of birth");
        if(author.getDateOfBirth()!= null){
            dateOfBirth.setValue(author.getDateOfBirth());
        }
        binder.forField(dateOfBirth)
                .bind(AuthorDto::getDateOfBirth, AuthorDto::setDateOfBirth);


        TextField placeOfBirth = new TextField();
        placeOfBirth.setWidth("50%");
        placeOfBirth.setLabel("Place of birth");
        if(author.getPlaceOfBirth() != null) {
            placeOfBirth.setValue(author.getPlaceOfBirth());
        }
        binder.forField(placeOfBirth)
                .bind(AuthorDto::getPlaceOfBirth, AuthorDto::setPlaceOfBirth);


        DatePicker dateOfDeath = new DatePicker();
        dateOfDeath.setWidth("50%");
        dateOfDeath.setLabel("Date of death");
        if(author.getDateOfDeath() != null){
            dateOfDeath.setValue(author.getDateOfDeath());
        }
        binder.forField(dateOfDeath)
                .bind(AuthorDto::getDateOfDeath, AuthorDto::setDateOfDeath);

        TextField genre = new TextField();
        genre.setWidth("50%");
        genre.setLabel("Genre");
        if(author.getGenre() != null){
            genre.setValue(author.getGenre());
        }
        binder.forField(genre)
                .bind(AuthorDto::getGenre, AuthorDto::setGenre);


        TextArea biography = new TextArea();
        biography.setWidth("100%");
        biography.setHeight("300px");
        biography.setLabel("Biography");
        biography.setClassName("dialog-biography");
        if(author.getBiography() != null){
            biography.setValue(author.getBiography());
        }
        binder.forField(biography)
                .bind(AuthorDto::getBiography, AuthorDto::setBiography);

        HorizontalLayout firstLine = new HorizontalLayout();
        firstLine.setWidthFull();
        firstLine.add(name);

        HorizontalLayout secondLine = new HorizontalLayout();
        secondLine.setWidthFull();
        secondLine.add(dateOfBirth, placeOfBirth);

        HorizontalLayout thirdLine = new HorizontalLayout();
        thirdLine.setWidthFull();
        thirdLine.add(dateOfDeath, genre);

        MemoryBuffer buffer = new MemoryBuffer();

        Div uploadButton = new Div();
        uploadButton.add("Upload file");
        uploadButton.setClassName("upload-button");

        Upload upload = new Upload(buffer);
        upload.setUploadButton(uploadButton);
        upload.setAcceptedFileTypes("image/*");
        upload.setMaxFiles(1);

        HorizontalLayout saveButtonLayout = new HorizontalLayout();
        saveButtonLayout.setWidthFull();
        saveButtonLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        saveButtonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Div saveButton = new Div();
        saveButton.add("Save");
        saveButton.setClassName("button");
        saveButton.addClickListener(onClick -> {
            try {
                binder.writeBeanIfValid(author);
                Optional<AuthorDto> updatedAuthor = authorRestClient.updateAuthor(author);
                if(updatedAuthor.isPresent()){
                    if(!authorRestClient.updateAuthorPhoto(updatedAuthor.get().getId(), buffer.getFileName(),
                            buffer.getFileData().getMimeType(),
                            buffer.getInputStream().readAllBytes())){
                        logger.error("Error to create image!");
                    }
                    super.close();
                    UI.getCurrent().getPage().reload();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        });

        saveButtonLayout.add(saveButton);

        mainLayout.add(labelLayout, new Hr(), firstLine, secondLine, thirdLine, upload, biography, saveButtonLayout);
        add(mainLayout);
    }
}
