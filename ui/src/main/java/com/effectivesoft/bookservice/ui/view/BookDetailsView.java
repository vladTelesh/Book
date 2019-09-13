package com.effectivesoft.bookservice.ui.view;

import com.effectivesoft.bookservice.ui.client.AuthorRestClient;
import com.effectivesoft.bookservice.ui.client.BookRestClient;
import com.effectivesoft.bookservice.ui.client.CommentRestClient;
import com.effectivesoft.bookservice.ui.client.UserRestClient;
import com.effectivesoft.bookservice.ui.component.*;
import com.effectivesoft.bookservice.ui.component.dialog.EditBookDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.effectivesoft.bookservice.common.dto.BookDto;
import com.effectivesoft.bookservice.common.dto.CommentDto;
import com.effectivesoft.bookservice.common.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.shared.ui.LoadMode;

import java.io.IOException;
import java.util.*;

@Route(value = "book")
@StyleSheet(value = "styles/bookDetailsViewStyle.css", loadMode = LoadMode.INLINE)
public class BookDetailsView extends HorizontalLayout implements HasUrlParameter<String>, HasDynamicTitle {
    private String bookId;
    private String title;

    private int commentsOnPage;
    private int commentsOnServer;
    private int commentsOffset = 3;

    private final BookRestClient bookRestClient;
    private final UserRestClient userRestClient;
    private final AuthorRestClient authorRestClient;
    private final CommentRestClient commentRestClient;

    private static final Logger logger = LoggerFactory.getLogger(BookDetailsView.class);

    public BookDetailsView(@Autowired BookRestClient bookRestClient,
                           @Autowired UserRestClient userRestClient,
                           @Autowired AuthorRestClient authorRestClient,
                           @Autowired CommentRestClient commentRestClient) {
        this.bookRestClient = bookRestClient;
        this.userRestClient = userRestClient;
        this.authorRestClient = authorRestClient;
        this.commentRestClient = commentRestClient;
    }

    private void load() throws IOException {
        BookDto book = this.bookRestClient.readBook(bookId);
        UserDto user = this.userRestClient.readUser();
        commentsOnServer = commentRestClient.readBookCommentsCount(bookId);
        if (book == null) {
            //todo: navigate to error page
            return;
        }
        title = "Book • " + book.getTitle();

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("70%");
        verticalLayout.setHeightFull();
        verticalLayout.setAlignItems(Alignment.CENTER);
        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout bookDetailsLayout = new HorizontalLayout();
        bookDetailsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        bookDetailsLayout.setWidthFull();

        VerticalLayout bookImageLayout = new VerticalLayout();
        bookImageLayout.setAlignItems(Alignment.CENTER);
        bookImageLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        bookImageLayout.setWidth("30%");

        VerticalLayout bookDetailsFieldsLayout = new VerticalLayout();
        bookDetailsFieldsLayout.setWidth("30%");

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setAlignItems(Alignment.CENTER);

        TextField title = new TextField("Title");
        title.setValue(book.getTitle());
        title.setReadOnly(true);

        Icon editIcon = new Icon(VaadinIcon.EDIT);
        editIcon.setSize("17px");
        editIcon.setClassName("edit-icon");
        editIcon.setColor("#4e4e4e");

        EditBookDialog editBookDialog = new EditBookDialog(book, bookRestClient, authorRestClient);

        editIcon.addClickListener(onClick -> editBookDialog.open());

        titleLayout.add(title, editIcon);

        TextField author = new TextField("Author");
        author.setValue(book.getAuthorName());
        author.setReadOnly(true);

        TextField additionalAuthors = new TextField("Additional authors");
        additionalAuthors.setValue(book.getAdditionalAuthors());
        additionalAuthors.setReadOnly(true);

        TextField isbn = new TextField("ISBN");
        isbn.setValue(book.getISBN());
        isbn.setReadOnly(true);

        TextField publicationYear = new TextField("Publication year");
        publicationYear.setValue(book.getPublicationYear().toString());
        publicationYear.setReadOnly(true);

        HorizontalLayout bookImageDivLayout = new HorizontalLayout();
        bookImageDivLayout.setWidthFull();
        bookImageDivLayout.setAlignItems(Alignment.CENTER);
        bookImageDivLayout.setJustifyContentMode(JustifyContentMode.CENTER);


        if(book.getImageLink() != null) {
            Image profileMainImage = new Image(book.getImageLink(), "");
            profileMainImage.setWidth("200px");
            bookImageDivLayout.add(profileMainImage);
        } else {
            Image profileMainImage = new Image("https://i.ibb.co/D7V4FC7/132226-200.png", "");
            profileMainImage.setWidth("200px");
            bookImageDivLayout.add(profileMainImage);
        }

        bookImageLayout.add(bookImageDivLayout, rating(book.getAverageRating()));
        bookDetailsFieldsLayout.add(titleLayout, author, additionalAuthors, isbn, publicationYear);

        bookDetailsLayout.add(bookImageLayout, bookDetailsFieldsLayout);

        TextArea description = new TextArea();
        description.setValue(book.getDescription());
        description.setWidth("70%");
        description.setReadOnly(true);

        HorizontalLayout commentsLabelLayout = new HorizontalLayout();
        commentsLabelLayout.setWidth("70%");

        Label commentsLabel = new Label("Comments:");
        commentsLabel.setClassName( "label");

        commentsLabelLayout.add(commentsLabel);

        HorizontalLayout descriptionLabelLayout = new HorizontalLayout();
        descriptionLabelLayout.setWidth("70%");

        Label descriptionLabel = new Label("Description:");
        descriptionLabel.setClassName("label");

        descriptionLabelLayout.add(descriptionLabel);

        Header header = new Header(userRestClient);

        VerticalLayout comments = new VerticalLayout();
        comments.setAlignItems(Alignment.CENTER);
        comments.setJustifyContentMode(JustifyContentMode.CENTER);
        loadCommentsPreview(comments, user);

        HorizontalLayout openAllCommentsButtonLayout = new HorizontalLayout();
        openAllCommentsButtonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        openAllCommentsButtonLayout.setAlignItems(Alignment.CENTER);

        Button openAllCommentsButton = new Button("Show more");
        openAllCommentsButton.addClickListener(onClick -> {
            try {
                loadComments(comments, user);
                if(commentsOnPage == commentsOnServer){
                    openAllCommentsButtonLayout.removeAll();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        });

        verticalLayout.add(header, new Hr(), bookDetailsLayout, descriptionLabelLayout, description, commentsLabelLayout,
                comments);

        if (commentsOnServer > commentsOnPage) {
            openAllCommentsButtonLayout.add(openAllCommentsButton);
            verticalLayout.add(openAllCommentsButtonLayout);
        }

        verticalLayout.add(openAllCommentsButtonLayout, new UserComment(commentRestClient, user, header.getProfileMainImageLink(), bookId, comments));

        add(verticalLayout);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String bookId) {
        this.bookId = bookId;

        try {
            load();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String getPageTitle() {
        return title;
    }

    private VerticalLayout rating(Double rating) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidthFull();
        verticalLayout.setAlignItems(Alignment.CENTER);
        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout ratingStars = new HorizontalLayout();
        ratingStars.setAlignItems(Alignment.CENTER);
        ratingStars.setJustifyContentMode(JustifyContentMode.CENTER);
        ratingStars.setWidthFull();

        String ratingStarColor = "#000000";

        if (rating >= 1 && rating < 2) {
            ratingStarColor = "#800000";
        }
        if (rating >= 2 && rating < 3) {
            ratingStarColor = "#A0522D";
        }
        if (rating >= 3 && rating < 4) {
            ratingStarColor = "#808000";
        }
        if (rating >= 4 && rating <= 5) {
            ratingStarColor = "#7FFF00";
        }

        ratingStars.add(new Label("Rating:"));

        for (int i = 0; i < rating.intValue(); i++) {
            Icon icon = new Icon(VaadinIcon.STAR);
            icon.setColor(ratingStarColor);
            ratingStars.add(icon);
        }

        if (rating % 1 != 0) {
            Icon icon = new Icon(VaadinIcon.STAR_HALF_LEFT_O);
            icon.setColor(ratingStarColor);
            ratingStars.add(icon);
            for (int i = rating.intValue(); i < 4; i++) {
                ratingStars.add(new Icon(VaadinIcon.STAR_O));
            }
        } else {
            for (int i = rating.intValue(); i < 5; i++) {
                ratingStars.add(new Icon(VaadinIcon.STAR_O));
            }
        }

        ratingStars.add(new Label(rating.toString()));

        HorizontalLayout rate = new HorizontalLayout();
        rate.setAlignItems(Alignment.CENTER);
        rate.setJustifyContentMode(JustifyContentMode.CENTER);
        rate.setWidthFull();

        NumberField userRating = new NumberField();
        userRating.setStep(1);
        userRating.setMax(5);
        userRating.setMin(1);
        userRating.setHasControls(true);


        rate.add(new Label("Rate:"), userRating, new Icon(VaadinIcon.CHECK_CIRCLE));


        verticalLayout.add(new Hr(), ratingStars, rate, new Hr());

        return verticalLayout;
    }

    private void loadComments(VerticalLayout verticalLayout, UserDto userDto) throws IOException {
        List<CommentDto> comments = commentRestClient.readBookComments(bookId, 10, commentsOffset);

        commentsOnPage += comments.size();

        if (!comments.isEmpty()) {
            for (CommentDto comment : comments) {
                verticalLayout.add(new CommentComponent(commentRestClient, comment, userDto));
            }
        }

        commentsOffset = 10;
    }

    private void loadCommentsPreview(VerticalLayout verticalLayout, UserDto userDto) throws IOException {
        List<CommentDto> comments = commentRestClient.readBookComments(bookId, 3, 0);

        commentsOnPage = comments.size();

        if (!comments.isEmpty()) {
            for (CommentDto comment : comments) {
                verticalLayout.add(new CommentComponent(commentRestClient, comment, userDto));
            }
        } else {
            Label label = new Label("No comments yet :(");
            label.setId("no_comments_label");
            verticalLayout.add(label);
        }
    }
}
