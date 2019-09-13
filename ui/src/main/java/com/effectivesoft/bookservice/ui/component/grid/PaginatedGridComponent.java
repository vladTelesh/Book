package com.effectivesoft.bookservice.ui.component.grid;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.awt.*;
import java.util.stream.Collectors;

@StyleSheet(value = "styles/gridButtonsStyle.css")
public class PaginatedGridComponent<T> extends Grid<T> {

    private Integer pageNumber = 1;

    public int getCurrentPage() {
        return pageNumber;
    }

    public HorizontalLayout createPaginationButtons(int count) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        for (int i = 0; i < count; i++) {
            Div button = new Div();
            button.add(String.valueOf(i + 1));
            if (i == 0) {
                button.setClassName("round-button-click");
            } else {
                button.setClassName("round-button");
            }
            button.addClickListener(onClick -> {
                onClick.getSource().getParent().get().getChildren()
                        .collect(Collectors.toList())
                        .stream().peek(btn -> {
                    if (((Div) btn).getClassName().equals("round-button")) {
                        ((Div) btn).setClassName("round-button-click");
                    } else {
                        ((Div) btn).setClassName("round-button");
                    }
                }).collect(Collectors.toList());
                pageNumber = Integer.parseInt(onClick.getSource().getId().get());
                getDataProvider().refreshAll();
            });
            button.setId(String.valueOf(i + 1));
            horizontalLayout.add(button);
        }
        return horizontalLayout;
    }
}
