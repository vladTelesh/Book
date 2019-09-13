package com.effectivesoft.bookservice.ui.component;

import com.effectivesoft.bookservice.common.dto.UserDto;
import com.effectivesoft.bookservice.ui.client.CommentRestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.effectivesoft.bookservice.common.dto.CommentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserComment extends HorizontalLayout {
    private static final Logger logger = LoggerFactory.getLogger(UserComment.class);

    public UserComment(CommentRestClient commentRestClient, UserDto userDto, String imageLink, String bookId, VerticalLayout comments) {
        setWidth("70%");

        VerticalLayout details = new VerticalLayout();
        details.setAlignItems(Alignment.CENTER);
        details.setJustifyContentMode(JustifyContentMode.CENTER);

        Image logo = new Image();
        logo.setSrc(imageLink);
        logo.setWidth("50px");
        logo.setHeight("50px");

        details.add(logo);
        details.setWidth("22%");

        TextArea textArea = new TextArea();
        textArea.setWidth("68%");
        textArea.setPlaceholder("Write anything about this book ...");

        VerticalLayout post = new VerticalLayout();
        post.setWidth("10%");
        post.setAlignItems(Alignment.CENTER);
        post.setJustifyContentMode(JustifyContentMode.CENTER);
        post.add(new Button("Post", onClick -> {
            if (textArea.getValue() != null && textArea.getValue().length() != 0) {
                CommentDto commentDto = new CommentDto();
                commentDto.setBookId(bookId);
                commentDto.setText(textArea.getValue());
                commentDto.setLiked(false);
                commentDto.setLikesCount(0);
                commentDto.setUserMainImageLink(imageLink);
                commentDto.setDateAdded(LocalDateTime.now());
                commentDto.setUserFirstName(userDto.getFirstName());
                commentDto.setUserLastName(userDto.getLastName());

                try {
                    if (commentRestClient.createBookComment(commentDto)) {
                        if (comments.getChildren().findFirst().flatMap(Component::getId).isPresent()) {
                            if (comments.getChildren().findFirst().flatMap(Component::getId).get().equals("no_comments_label")) {
                                comments.removeAll();
                            }
                        }
                        textArea.setValue("");
                        comments.addComponentAsFirst(new CommentComponent(commentRestClient, commentDto, userDto));
                    }
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage());
                }
            }
        }));

        add(details, textArea, post);

        getElement().removeAttribute("theme")
                .setAttribute("theme", "spacing");
        setClassName("user-comment");
    }
}
