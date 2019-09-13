package com.effectivesoft.bookservice.ui.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class BookPreviewComponent extends VerticalLayout {

    public BookPreviewComponent(String id, String title, String imageLink) {
        setAlignItems(Alignment.CENTER);
        setWidth("45%");

        Image image = new Image(imageLink, "");
        image.setClassName("image");
        image.setWidth("60.5px");
        image.setHeight("96.8px");
        image.addClickListener(onClick -> {
            UI.getCurrent().navigate("book/" + id);
        });

        Anchor anchor = new Anchor("book/" + id, title);
        anchor.setClassName("anchor");
        add(image, anchor);

        getElement().removeAttribute("theme");
    }
}
