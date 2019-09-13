package com.effectivesoft.bookservice.ui.view;


import com.effectivesoft.bookservice.common.dto.AuthorDto;
import com.effectivesoft.bookservice.ui.client.AuthorRestClient;
import com.effectivesoft.bookservice.ui.client.UserRestClient;
import com.effectivesoft.bookservice.ui.component.AuthorPreviewComponent;
import com.effectivesoft.bookservice.ui.component.Header;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Route("authors")
@PageTitle("Authors • Book-service")
@StyleSheet("styles/authorsListViewStyle.css")
public class AuthorsListView extends HorizontalLayout {

    private final UserRestClient userRestClient;
    private final AuthorRestClient authorRestClient;

    private final Integer AUTHORS_ON_PAGE = 12;
    private int onPage = 0;

    private static final Logger logger = LoggerFactory.getLogger(AuthorsListView.class);

    private VerticalLayout authorsLayout = new VerticalLayout();
    private HorizontalLayout showMoreButtonLayout = new HorizontalLayout();
    private Div showMoreButton = new Div();

    public AuthorsListView(@Autowired UserRestClient userRestClient,
                           @Autowired AuthorRestClient authorRestClient) throws IOException {
        this.userRestClient = userRestClient;
        this.authorRestClient = authorRestClient;

        authorsLayout.setWidthFull();
        authorsLayout.getElement().removeAttribute("theme");
        authorsLayout.getElement().setAttribute("theme", "spacing");

        showMoreButtonLayout.setWidthFull();
        showMoreButtonLayout.setAlignItems(Alignment.CENTER);
        showMoreButtonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        showMoreButton.setClassName("show-more-button");
        showMoreButton.add("Show more");

        load();
    }


    private void load() throws IOException {
        int count = authorRestClient.readAuthorsCount("", false);

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("70%");
        verticalLayout.setHeightFull();
        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        verticalLayout.setAlignItems(Alignment.CENTER);

        HorizontalLayout fieldsLayout = new HorizontalLayout();
        fieldsLayout.setAlignItems(Alignment.CENTER);
        fieldsLayout.setWidthFull();

        TextField nameField = new TextField();
        nameField.setValueChangeMode(ValueChangeMode.EAGER);
        nameField.setLabel("Name");


        Checkbox checkbox = new Checkbox();
        checkbox.setClassName("checkbox");
        checkbox.setLabel("Show only auto-generated authors");
        checkbox.addClickListener(onClick -> {
            try {
                fillTable(nameField.getValue(), checkbox.getValue(), false);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        });

        nameField.addValueChangeListener(value -> {
            try {
                fillTable(nameField.getValue(), checkbox.getValue(), false);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        });

        fieldsLayout.add(nameField, checkbox);

        fillTable("", false, false);

        verticalLayout.add(new Header(userRestClient), new Hr(), fieldsLayout, authorsLayout);

        showMoreButton.addClickListener(onClick -> {
            try {
                fillTable(nameField.getValue(), checkbox.getValue(), true);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        });

        if (count > AUTHORS_ON_PAGE) {
            showMoreButtonLayout.add(showMoreButton);
            verticalLayout.add(showMoreButtonLayout);
        }

        add(verticalLayout);
    }

    private void fillTable(String name, boolean generated, boolean more) throws IOException {
        if (!more) {
            onPage = 0;
            authorsLayout.removeAll();
        }
        showMoreButtonLayout.removeAll();

        List<AuthorDto> authors = authorRestClient.readAuthors(name, generated, AUTHORS_ON_PAGE, onPage, Collections.emptyList());
        int count = authorRestClient.readAuthorsCount(name, generated);

        int i = 0;

        while (i < authors.size() - 3) {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setWidthFull();

            for (int j = i; j < i + 3; j++) {
                AuthorDto author = authors.get(j);
                AuthorPreviewComponent authorPreviewComponent = new AuthorPreviewComponent(author.getPhotoLink(),
                        author.getName(), author.getGenre());
                authorPreviewComponent.addClickListener(onClick -> {
                    UI.getCurrent().navigate("author/" + author.getId());
                });
                horizontalLayout.add(authorPreviewComponent);
                onPage++;
            }
            authorsLayout.add(horizontalLayout);
            i += 3;
        }

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        for (int j = i; j < authors.size(); j++) {
            AuthorDto author = authors.get(j);
            AuthorPreviewComponent authorPreviewComponent = new AuthorPreviewComponent(author.getPhotoLink(),
                    author.getName(), author.getGenre());
            authorPreviewComponent.addClickListener(onClick -> {
                UI.getCurrent().navigate("author/" + author.getId());
            });
            onPage++;
            horizontalLayout.add(authorPreviewComponent);
        }

        authorsLayout.add(horizontalLayout);

        if (onPage < count) {
            showMoreButtonLayout.add(showMoreButton);
        }
    }
}
