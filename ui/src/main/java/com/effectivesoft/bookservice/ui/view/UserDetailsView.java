package com.effectivesoft.bookservice.ui.view;

import com.effectivesoft.bookservice.ui.client.UserRestClient;
import com.effectivesoft.bookservice.ui.component.Header;
import com.effectivesoft.bookservice.ui.component.ImagePicker;
import com.effectivesoft.bookservice.ui.config.security.SecurityContextParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.effectivesoft.bookservice.common.dto.PasswordsDto;
import com.effectivesoft.bookservice.common.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Objects;


@Route(value = "profile")
@StyleSheet("styles/userDetailsViewStyle.css")
public class UserDetailsView extends HorizontalLayout implements HasDynamicTitle {
    private String title;
    private boolean isEdit = false;

    private final UserRestClient userRestClient;
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsView.class);


    public UserDetailsView(@Autowired UserRestClient userRestClient) throws IOException {
        this.userRestClient = userRestClient;
        UserDto user = this.userRestClient.readUser();
        UserDto updatedUser = new UserDto();
        if (user == null) {
            SecurityContextHolder.clearContext();
            UI.getCurrent().navigate("sign_in");
            return;
        }

        title = "Profile • " + SecurityContextParser.getEmail() + " • Book-service";

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("70%");
        verticalLayout.setAlignItems(Alignment.CENTER);
        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        Header header = new Header(userRestClient);
        verticalLayout.add(header, new Hr());

        HorizontalLayout profileInfoLayout = new HorizontalLayout();

        VerticalLayout imageFields = new VerticalLayout();

        HorizontalLayout profileImageLayout = new HorizontalLayout();
        profileImageLayout.setWidthFull();
        profileImageLayout.setAlignItems(Alignment.CENTER);
        profileImageLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        Image profileMainImage = new Image(header.getProfileMainImageLink(), "");
        profileMainImage.setWidth("350px");
        profileMainImage.setHeight("350px");
        profileImageLayout.add(profileMainImage);

        ImagePicker imagePicker = new ImagePicker(userRestClient, profileMainImage);

        VerticalLayout infoFields = new VerticalLayout();

        Binder<UserDto> binder = new Binder<>();

        TextField firstName = new TextField("First name");
        firstName.setWidthFull();
        firstName.setReadOnly(true);
        firstName.setValue(user.getFirstName());
        binder.forField(firstName)
                .withValidator(value -> value.length() >= 4, "First name must contain at least 4 characters")
                .bind(UserDto::getFirstName, UserDto::setFirstName);
        TextField lastName = new TextField("Last name");
        lastName.setWidthFull();
        lastName.setReadOnly(true);
        if (user.getLastName() != null) {
            lastName.setValue(user.getLastName());
        } else {
            lastName.setValue("");
        }
        binder.forField(lastName)
                .withValidator(value -> value.length() >= 4, "Last name must contain at least 4 characters")
                .bind(UserDto::getLastName, UserDto::setLastName);
        TextField email = new TextField("Email");
        email.setWidth("260px");
        email.setReadOnly(true);
        email.setValue(user.getUsername());
        DatePicker dateOfBirth = new DatePicker("Date of birth");
        dateOfBirth.setWidthFull();
        dateOfBirth.setReadOnly(true);
        dateOfBirth.setValue(user.getDateOfBirth());
        binder.forField(dateOfBirth)
                .withValidator(Objects::nonNull, "Date field can't be empty")
                .bind(UserDto::getDateOfBirth, UserDto::setDateOfBirth);


        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setAlignItems(Alignment.CENTER);
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        Div edit = new Div();
        edit.add("Edit");
        edit.setClassName("edit-button");

        Dialog addImageDialog = new Dialog();

        MemoryBuffer buffer = new MemoryBuffer();

        Upload upload = new Upload(buffer);
        upload.setUploadButton(new Button("Upload file"));
        upload.setAcceptedFileTypes("image/*");

        HorizontalLayout saveImageButtonLayout = new HorizontalLayout();
        saveImageButtonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        saveImageButtonLayout.setAlignItems(Alignment.CENTER);
        saveImageButtonLayout.add(new Button("Save", onClick -> {
            if (buffer.getFileData() != null && !upload.isUploading()) {
                try {
                    if (userRestClient.createUserImage(buffer.getFileName(),
                            buffer.getFileData().getMimeType(),
                            buffer.getInputStream().readAllBytes())) {
                        imagePicker.refreshImages(profileMainImage);
                        addImageDialog.close();
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }));

        addImageDialog.add(upload, saveImageButtonLayout);

        Div addButton = new Div();
        addButton.add("Add photo");
        addButton.setClassName("add-button");
        addButton.addClickListener(onClick -> addImageDialog.open());

        Dialog changePasswordDialog = new Dialog();
        VerticalLayout changePasswordFields = new VerticalLayout();
        changePasswordFields.setAlignItems(Alignment.CENTER);
        changePasswordFields.setJustifyContentMode(JustifyContentMode.CENTER);

        Binder<PasswordsDto> passwordsBinder = new Binder<>();
        PasswordField currentPassword = new PasswordField();
        currentPassword.setPlaceholder("Current password");
        passwordsBinder.forField(currentPassword)
                .withValidator(value -> value.length() >= 6, "Password must contain at least 6 characters")
                .bind(PasswordsDto::getCurrentPassword, PasswordsDto::setCurrentPassword);
        PasswordField newPassword = new PasswordField();
        newPassword.setPlaceholder("New password");
        passwordsBinder.forField(newPassword)
                .withValidator(value -> value.length() >= 6, "Password must contain at least 6 characters")
                .bind(PasswordsDto::getNewPassword, PasswordsDto::setNewPassword);
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPlaceholder("Confirm password");
        passwordsBinder.forField(confirmPassword)
                .withValidator(value -> value.equals(newPassword.getValue()),
                        "Your password and confirmation password don't match")
                .bind(PasswordsDto::getConfirmPassword, PasswordsDto::setConfirmPassword);
        Button change = new Button("Change", onClick -> {
            if (passwordsBinder.validate().isOk()) {
                PasswordsDto passwords = new PasswordsDto();
                passwordsBinder.writeBeanIfValid(passwords);
                try {
                    if (userRestClient.updateUserPassword(passwords)) {
                        changePasswordDialog.close();
                    } else {
                        currentPassword.setInvalid(true);
                    }
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage());
                }
            }
        });

        changePasswordFields.add(currentPassword, new Hr(), newPassword, confirmPassword, change);

        changePasswordDialog.add(changePasswordFields);

        Button changePassword = new Button("Change password", onClick -> {
            changePasswordDialog.open();
        });

        changePassword.setWidthFull();
        changePassword.setEnabled(false);

        edit.addClickListener(onClick -> {
            if (!isEdit) {
                firstName.setReadOnly(false);
                lastName.setReadOnly(false);
                dateOfBirth.setReadOnly(false);
                changePassword.setEnabled(true);
                edit.setText("Save");
                edit.setClassName("save-button");
                isEdit = true;
            } else {
                if (!binder.validate().isOk()) {
                    return;
                }
                if (isChanged(user, firstName, lastName, dateOfBirth)) {
                    binder.writeBeanIfValid(updatedUser);
                    try {
                        if (userRestClient.updateUser(updatedUser)) {
                            user.setFirstName(updatedUser.getFirstName());
                            user.setLastName(updatedUser.getLastName());
                            user.setDateOfBirth(updatedUser.getDateOfBirth());
                        } else {
                            UI.getCurrent().getPage().reload();
                        }
                    } catch (JsonProcessingException e) {
                        logger.error(e.getMessage());
                    }
                }
                firstName.setReadOnly(true);
                lastName.setReadOnly(true);
                dateOfBirth.setReadOnly(true);
                changePassword.setEnabled(false);
                edit.setText("Edit");
                edit.setClassName("edit-button");
                isEdit = false;
            }
        });

        buttonLayout.add(edit);

        VerticalLayout horizontalMargin = new VerticalLayout();

        infoFields.add(firstName, lastName, email, dateOfBirth, changePassword, buttonLayout);

        HorizontalLayout changePhotoButtonLayout = new HorizontalLayout();
        changePhotoButtonLayout.setWidthFull();
        changePhotoButtonLayout.setAlignItems(Alignment.CENTER);
        changePhotoButtonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        changePhotoButtonLayout.add(addButton);

        imageFields.add(profileImageLayout, new Hr(), imagePicker, changePhotoButtonLayout);

        profileInfoLayout.add(imageFields, horizontalMargin, infoFields);

        HorizontalLayout verticalMargin = new HorizontalLayout();
        verticalMargin.setHeight("50px");

        verticalLayout.add(verticalMargin, profileInfoLayout);

        add(verticalLayout);
    }


    @Override
    public String getPageTitle() {
        return title;
    }

    private boolean isChanged(UserDto user, TextField firstName, TextField lastName, DatePicker dateOfBirth) {
        if (user.getDateOfBirth() == null) {
            return true;
        }

        return !user.getFirstName().equals(firstName.getValue().trim())
                || !user.getLastName().equals(lastName.getValue().trim())
                || !user.getDateOfBirth().equals(dateOfBirth.getValue());
    }
}
