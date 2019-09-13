package com.effectivesoft.bookservice.ui.component;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class AuthorPreviewComponent extends HorizontalLayout implements ClickNotifier<AuthorPreviewComponent> {

    public AuthorPreviewComponent(String photoLink, String name, String info){
        setClassName("author-preview-component");

        setHeight("120px");
        setWidth("32.5%");

        VerticalLayout photoLayout = new VerticalLayout();
        photoLayout.getElement().removeAttribute("theme");
        photoLayout.setWidth("85px");

        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.getElement().removeAttribute("theme");
        infoLayout.setHeightFull();

        HorizontalLayout firstLine = new HorizontalLayout();
        firstLine.setWidthFull();

        HorizontalLayout secondLine = new HorizontalLayout();
        secondLine.setWidthFull();

        Label nameLabel = new Label(name);
        nameLabel.setClassName("author-preview-name-label");

        Label infoLabel = new Label(info);
        infoLabel.setClassName("author-preview-info-label");

        Image photo = new Image(photoLink, "");
        photo.setWidth("82px");
        photo.setHeightFull();

        photoLayout.add(photo);

        firstLine.add(nameLabel);

        secondLine.add(infoLabel);

        Hr hr = new Hr();
        hr.setClassName("hr");

        infoLayout.add(firstLine, hr, secondLine);

        add(photoLayout, infoLayout);
    }
}
