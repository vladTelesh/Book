package com.effectivesoft.bookservice.ui.component;

import com.vaadin.flow.component.*;

@Tag("likesCount")
public class LikesCountComponent extends HtmlContainer implements ClickNotifier<LikesCountComponent> {


    public LikesCountComponent() {
        super();
    }

    public LikesCountComponent(String s){
        this();
        setText(s);
    }
}