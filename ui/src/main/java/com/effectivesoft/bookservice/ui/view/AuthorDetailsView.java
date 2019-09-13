package com.effectivesoft.bookservice.ui.view;

import com.effectivesoft.bookservice.common.dto.AuthorDto;
import com.effectivesoft.bookservice.common.dto.BookDto;
import com.effectivesoft.bookservice.ui.client.AuthorRestClient;
import com.effectivesoft.bookservice.ui.client.UserRestClient;
import com.effectivesoft.bookservice.ui.component.BookPreviewComponent;
import com.effectivesoft.bookservice.ui.component.dialog.EditAuthorDialog;
import com.effectivesoft.bookservice.ui.component.Header;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;

@Route("author")
@StyleSheet("styles/authorDetailsViewStyle.css")
public class AuthorDetailsView extends HorizontalLayout implements HasUrlParameter<String>, HasDynamicTitle {
    private String title;
    private String authorId;

    private final AuthorRestClient authorRestClient;
    private final UserRestClient userRestClient;
    private static final Logger logger = LoggerFactory.getLogger(AuthorDetailsView.class);

    public AuthorDetailsView(@Autowired UserRestClient userRestClient,
                             @Autowired AuthorRestClient authorRestClient) {
        this.userRestClient = userRestClient;
        this.authorRestClient = authorRestClient;
    }

    private void load() throws IOException {
        Optional<AuthorDto> optionalAuthor = authorRestClient.readAuthor(authorId);
        if (optionalAuthor.isEmpty()) {
            //todo: Navigate to error page
            return;
        }
        AuthorDto author = optionalAuthor.get();
        List<BookDto> booksPreview = authorRestClient.readAuthorsBooks(author.getId(), 4, 0);
        title = "Author • " + author.getName() + " • Book-service";

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("70%");
        verticalLayout.setHeightFull();
        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        verticalLayout.setAlignItems(Alignment.CENTER);

        VerticalLayout authorDetailsLayout = new VerticalLayout();
        authorDetailsLayout.setWidthFull();
        authorDetailsLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout authorDetailsFieldsLayout = new HorizontalLayout();
        authorDetailsFieldsLayout.setWidthFull();
        authorDetailsFieldsLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout authorImageLayout = new VerticalLayout();
        authorImageLayout.setAlignItems(Alignment.CENTER);
        authorImageLayout.setWidth("30%");

        Image authorPhoto = new Image();
        if (author.getPhotoLink() != null) {
            authorPhoto.setSrc(author.getPhotoLink());
        } else {
            authorPhoto.setSrc("https://i.ibb.co/WFBPVBc/avatar.png");
        }

        authorPhoto.setWidth("200px");

        authorImageLayout.add(authorPhoto);


        VerticalLayout authorInfoFieldsLayout = new VerticalLayout();
        authorInfoFieldsLayout.setWidth("40%");
        
        HorizontalLayout authorHeaderLayout = new HorizontalLayout();
        authorHeaderLayout.setWidthFull();
        authorHeaderLayout.setAlignItems(Alignment.CENTER);

        Label nameLabel = new Label(author.getName());
        nameLabel.setClassName("author-name-label");

        Icon editIcon = new Icon(VaadinIcon.EDIT);
        editIcon.setSize("17px");
        editIcon.setColor("#4e4e4e");
        editIcon.setClassName("edit-icon");

        EditAuthorDialog editAuthorDialog = new EditAuthorDialog(author, authorRestClient);

        editIcon.addClickListener(onClick -> editAuthorDialog.open());

        authorHeaderLayout.add(nameLabel, editIcon);

        HorizontalLayout authorInfoLayout = new HorizontalLayout();
        authorInfoLayout.setWidthFull();
        authorInfoLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout dataPointsLayout = new VerticalLayout();
        dataPointsLayout.getElement().removeAttribute("theme");
        dataPointsLayout.setWidth("30%");

        VerticalLayout dataLayout = new VerticalLayout();
        dataLayout.getElement().removeAttribute("theme");
        dataLayout.setWidth("70%");

        authorInfoLayout.add(dataPointsLayout, dataLayout);


        this.setBorn(author, dataPointsLayout, dataLayout);

        this.setDied(author, dataPointsLayout, dataLayout);

        this.setGenre(author, dataPointsLayout, dataLayout);


        authorInfoLayout.add(dataPointsLayout, dataLayout);

        authorInfoFieldsLayout.add(authorHeaderLayout, new Hr(), authorInfoLayout);

        authorDetailsFieldsLayout.add(authorImageLayout, authorInfoFieldsLayout);

        authorDetailsLayout.add(authorDetailsFieldsLayout);

        verticalLayout.add(new Header(userRestClient), new Hr(), authorDetailsLayout);

        this.setBiography(author, authorInfoFieldsLayout);

        this.setBooksPreview(booksPreview, authorImageLayout);

        add(verticalLayout);
    }

    @Override
    public String getPageTitle() {
        return title;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String authorId) {
        this.authorId = authorId;

        try {
            load();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void setBorn(AuthorDto author, VerticalLayout dataPointsLayout, VerticalLayout dataLayout) {
        Label born = new Label("Born:");
        born.setClassName("bold-label");

        Label emptyLabel = new Label("empty");
        emptyLabel.setClassName("hidden-label");

        if (author.getPlaceOfBirth() != null && author.getDateOfBirth() != null) {
            Label bornDataPlace = new Label(author.getPlaceOfBirth());
            bornDataPlace.setClassName("normal-label");

            Label bornDataDate = new Label(author.getDateOfBirth().format(DateTimeFormatter
                    .ofLocalizedDate(FormatStyle.LONG)));
            bornDataDate.setClassName("normal-label-with-margin-3");

            dataPointsLayout.add(born, emptyLabel);
            dataLayout.add(bornDataPlace, bornDataDate);
        } else {
            if (author.getPlaceOfBirth() != null) {
                Label bornDataPlace = new Label(author.getPlaceOfBirth());
                bornDataPlace.setClassName("normal-label");


                dataPointsLayout.add(born);
                dataLayout.add(bornDataPlace);
            }
            if (author.getDateOfBirth() != null) {
                Label bornDataDate = new Label(author.getDateOfBirth().format(DateTimeFormatter
                        .ofLocalizedDate(FormatStyle.LONG)));
                bornDataDate.setClassName("normal-label");

                dataPointsLayout.add(born);
                dataLayout.add(bornDataDate);
            }
        }
    }

    private void setDied(AuthorDto author, VerticalLayout dataPointsLayout, VerticalLayout dataLayout) {
        Label died = new Label("Died:");
        died.setClassName("bold-label-with-margin");

        if (author.getDateOfDeath() != null) {
            Label diedData = new Label(author.getDateOfDeath().format(DateTimeFormatter
                    .ofLocalizedDate(FormatStyle.LONG)));
            diedData.setClassName("normal-label-with-margin-12");

            dataPointsLayout.add(died);
            dataLayout.add(diedData);
        }
    }

    private void setGenre(AuthorDto author, VerticalLayout dataPointsLayout, VerticalLayout dataLayout) {
        Label genre = new Label("Genre:");
        genre.setClassName("bold-label-with-margin");

        if (author.getGenre() != null) {
            Label genreData = new Label(author.getGenre());
            genreData.setClassName("normal-label-with-margin-12");

            dataPointsLayout.add(genre);
            dataLayout.add(genreData);
        }
    }

    private void setBiography(AuthorDto author, VerticalLayout authorInfoFieldsLayout) {
        if (author.getBiography() != null) {
            VerticalLayout textLayout = new VerticalLayout();
            textLayout.setClassName("text-area");
            textLayout.setWidthFull();

            TextArea biography = new TextArea();
            biography.setValue(author.getBiography());
            biography.setWidthFull();
            biography.setReadOnly(true);

            Label biographyLabel = new Label("Biography:");
            biographyLabel.setClassName("bold-label-with-margin");
            authorInfoFieldsLayout.add(biographyLabel, biography);
        }
    }

    private void setBooksPreview(List<BookDto> booksPreview, VerticalLayout authorImageLayout) {
        if (booksPreview != null && booksPreview.size() != 0) {

            Label authorsBooks = new Label("Author's books:");
            authorsBooks.setClassName("bold-label-with-margin-40");

            HorizontalLayout booksPreviewLayout = new HorizontalLayout();
            booksPreviewLayout.setClassName("books-preview-layout");
            booksPreviewLayout.setWidth("200px");
            booksPreviewLayout.setJustifyContentMode(JustifyContentMode.CENTER);

            booksPreviewLayout.add(new BookPreviewComponent(booksPreview.get(0).getId(),
                    booksPreview.get(0).getTitle(),
                    booksPreview.get(0).getImageLink()));
            if (booksPreview.size() > 1) {
                booksPreviewLayout.add(new BookPreviewComponent(booksPreview.get(1).getId(),
                        booksPreview.get(1).getTitle(),
                        booksPreview.get(1).getImageLink()));
            }

            authorImageLayout.add(authorsBooks, booksPreviewLayout);

            if (booksPreview.size() > 2) {
                HorizontalLayout booksPreviewLayout1 = new HorizontalLayout();
                booksPreviewLayout1.setWidth("200px");
                booksPreviewLayout1.setJustifyContentMode(JustifyContentMode.CENTER);

                booksPreviewLayout1.add(new BookPreviewComponent(booksPreview.get(2).getId(),
                        booksPreview.get(2).getTitle(),
                        booksPreview.get(2).getImageLink()));
                if (booksPreview.size() > 3) {
                    booksPreviewLayout1.add(new BookPreviewComponent(booksPreview.get(3).getId(),
                            booksPreview.get(3).getTitle(),
                            booksPreview.get(3).getImageLink()));
                }
                authorImageLayout.add(booksPreviewLayout1);
            }

            HorizontalLayout showAllButtonLayout = new HorizontalLayout();
            showAllButtonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
            showAllButtonLayout.setAlignItems(Alignment.CENTER);
            showAllButtonLayout.setWidth("200px");

            Div button = new Div();
            button.add("Show more");
            button.addClickListener(onClick -> {

            });

            button.setClassName("button");

            showAllButtonLayout.add(button);

            authorImageLayout.add(showAllButtonLayout);
        }
    }
}
