package com.effectivesoft.bookservice.ui.component;

import com.effectivesoft.bookservice.ui.client.UserRestClient;
import com.effectivesoft.bookservice.ui.config.security.SecurityContextParser;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

@StyleSheet("styles/headerStyle.css")
public class Header extends HorizontalLayout {

    private String profileMainImageLink;

    public Header(@Autowired UserRestClient userRestClient) throws IOException {
        this.profileMainImageLink = userRestClient.readUserMainImage();
        setWidthFull();

        HorizontalLayout logo = new HorizontalLayout();
        logo.setAlignItems(Alignment.CENTER);
        logo.setJustifyContentMode(JustifyContentMode.START);
        Image image = new Image();
        image.addClickListener(onClick -> {
            UI.getCurrent().navigate("books");
        });
        image.setClassName("logo");
        image.setSrc("https://i.ibb.co/zZ5fWvR/ES-logo.png");
        image.setHeight("30px");
        logo.add(image);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setWidthFull();
        buttons.setAlignItems(Alignment.CENTER);
        buttons.setJustifyContentMode(JustifyContentMode.END);

        Div addAuthorButton = new Div();
        addAuthorButton.add("New author");
        addAuthorButton.setClassName("header-button");
        addAuthorButton.addClickListener(onClick -> UI.getCurrent().navigate("new/author"));

        Div addBookButton = new Div();
        addBookButton.add("New book");
        addBookButton.setClassName("header-button");
        addBookButton.addClickListener(onClick -> UI.getCurrent().navigate("new/book"));

        Div myBooksButton = new Div();
        myBooksButton.add("Books");
        myBooksButton.setClassName("my-books-button");
        myBooksButton.addClickListener(onClick -> UI.getCurrent().navigate("my_books"));

        Div authorsButton = new Div();
        authorsButton.add("Authors");
        authorsButton.setClassName("header-button");
        authorsButton.addClickListener(onClick -> UI.getCurrent().navigate("authors"));

        buttons.add(addAuthorButton, addBookButton, authorsButton);

        Select<Button> select = new Select<>();
        select.setClassName("select");
        select.setEmptySelectionAllowed(true);

        String username = SecurityContextParser.getEmail();

        select.setEmptySelectionCaption(username);
        select.addComponents(null, new Hr());

        Div profile = new Div();
        profile.setClassName("header-button");
        profile.add("Profile");
        profile.addClickListener(onClick -> UI.getCurrent().navigate("profile"));
        select.add(profile);

        Div stats = new Div();
        stats.setClassName("stats-button");
        stats.add("Stats");
        stats.addClickListener(onClick -> {
            UI.getCurrent().navigate("stats");
        });

        Div settings = new Div();
        settings.setClassName("settings-button");
        settings.add("Settings");
        settings.addClickListener(onClick -> {
            UI.getCurrent().navigate("settings");
        });

        select.add(profile, myBooksButton, stats, settings);

        select.addComponentAtIndex(6, new Hr());

        Div signOut = new Div();
        signOut.setClassName("sign-out-button");
        signOut.add("Sign out");
        signOut.addClickListener(onClick -> {
            SecurityContextHolder.clearContext();
            UI.getCurrent().navigate("sign_in");
            UI.getCurrent().getPage().reload();
        });

        select.add(signOut);

        Image profileMainImage = new Image(this.profileMainImageLink, "");
        profileMainImage.setWidth("25px");
        profileMainImage.setHeight("25px");
        Div iconWrapper = new Div();
        iconWrapper.add(profileMainImage);

        select.addToPrefix(iconWrapper, new Hr());

        select.setWidth("" + (username.length() * 12 + 3) + "px");

        buttons.add(select);

        add(logo, buttons);
    }

    public String getProfileMainImageLink() {
        return profileMainImageLink;
    }
}
