package com.effectivesoft.bookservice.ui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "sign_in")
@PageTitle("Sign in")
@StyleSheet("styles/loginViewStyle.css")
public class SignInView extends VerticalLayout {

    public SignInView() {
        LoginOverlay login = new LoginOverlay();
        login.setForgotPasswordButtonVisible(false);
        login.setAction("login");
        login.setOpened(true);
        login.setTitle("EffectiveSoft");
        login.setDescription("Book-service");

        add(login);

        UI.getCurrent().getPage().executeJavaScript("const div = document.createElement(\"div\");\n" +
                "div.className = \"google-login-div\";\n" +
                "const a = document.createElement(\"a\");\n" +
                "a.href = \"http://localhost:8080/oauth2/authorization/google\";\n" +
                "a.text = \"Sign in with Google\";\n" +
                "div.appendChild(a);\n" +
                "document.getElementById(\"vaadinLoginForm\").appendChild(div);");
    }
}