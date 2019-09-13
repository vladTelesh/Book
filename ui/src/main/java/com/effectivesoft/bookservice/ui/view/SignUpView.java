package com.effectivesoft.bookservice.ui.view;

import com.effectivesoft.bookservice.ui.client.UserRestClient;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.effectivesoft.bookservice.common.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Objects;

@PageTitle("Sign Up")
@Route(value = "sign_up")
public class SignUpView extends VerticalLayout {
    private final UserRestClient userRestClient;

    private static final Logger logger = LoggerFactory.getLogger(SignUpView.class);

    public SignUpView(@Autowired UserRestClient userRestClient) {
        this.userRestClient = userRestClient;

        setHeightFull();
        FormLayout layout = new FormLayout();

        TextField firstName = new TextField();
        firstName.setValueChangeMode(ValueChangeMode.ON_BLUR);
        TextField lastName = new TextField();
        lastName.setValueChangeMode(ValueChangeMode.ON_BLUR);
        TextField email = new TextField();
        email.setValueChangeMode(ValueChangeMode.ON_BLUR);
        PasswordField password = new PasswordField();
        password.setValueChangeMode(ValueChangeMode.ON_BLUR);
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setValueChangeMode(ValueChangeMode.ON_BLUR);
        DatePicker dateOfBirth = new DatePicker();

        layout.setWidth("10%");

        layout.addFormItem(firstName, "First name");
        layout.addFormItem(lastName, "Last name");
        layout.addFormItem(dateOfBirth, "Date of birth");
        layout.addFormItem(email, "Email");
        layout.addFormItem(password, "Password");
        layout.addFormItem(confirmPassword, "Confirm password");
        Button signUpButton = new Button("Sign up");
        add(layout);
        add(signUpButton);

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        UserDto user = new UserDto();
        Binder<UserDto> userBinder = new Binder<>(UserDto.class);

        userBinder.forField(firstName)
                .withValidator(name -> name.length() >= 4, "First name must contain at least 4 characters")
                .bind(UserDto::getFirstName, UserDto::setFirstName);
        userBinder.forField(lastName)
                .withValidator(new StringLengthValidator(
                        "Last name must contain at least 4 characters", 4, 20))
                .bind(UserDto::getLastName, UserDto::setLastName);
        userBinder.forField(dateOfBirth)
                .withValidator(Objects::nonNull, "Date field can't be empty")
                .bind(UserDto::getDateOfBirth, UserDto::setDateOfBirth);
        userBinder.forField(email)
                .withValidator(new EmailValidator(
                        "This doesn't look like a valid email address"))
                .bind(UserDto::getUsername, UserDto::setUsername);
        userBinder.forField(password)
                .withValidator(new StringLengthValidator(
                        "Password must contain at least 6 characters", 6, 20))
                .bind(UserDto::getPassword, UserDto::setPassword);
        userBinder.forField(confirmPassword)
                .withValidator(
                        pass -> (pass.equals(password.getValue())),
                        "Your password and confirmation password don't match")
                .bind(UserDto::getConfirmPassword, UserDto::setConfirmPassword);

        signUpButton.addClickListener(onClick -> {
            if (userBinder.validate().isOk()) {
                userBinder.writeBeanIfValid(user);
                try {
                    if (this.userRestClient.createUser(user)) {
                        UI.getCurrent().navigate("confirmation");
                    } else {
                        add(new Text("Such user already exist"));
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        });
    }
}
