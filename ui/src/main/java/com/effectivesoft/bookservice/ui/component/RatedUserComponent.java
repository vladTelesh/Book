package com.effectivesoft.bookservice.ui.component;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class RatedUserComponent extends HorizontalLayout {

    public RatedUserComponent(String imageLink, String firstName, String lastName) {
        setAlignItems(Alignment.CENTER);
        Image image = new Image(imageLink, "");
        image.setWidth("50px");
        image.setHeight("50px");
        add(image, new Label(firstName + " " + lastName));
    }
}
