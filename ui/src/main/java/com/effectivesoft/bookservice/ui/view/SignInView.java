package com.effectivesoft.bookservice.ui.view;

import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("sign_in")
@PageTitle("Sing in")
public class SignInView extends VerticalLayout {

    public SignInView() {
        LoginOverlay login = new LoginOverlay();
        login.setForgotPasswordButtonVisible(false);
        login.setAction("login");
        login.setOpened(true);
        login.setTitle("EffectiveSoft");
        login.setDescription("Book-service");
        getElement().appendChild(login.getElement());
    }
}