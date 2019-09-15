package com.effectivesoft.bookservice.ui.component;

import com.effectivesoft.bookservice.common.dto.UserDto;
import com.effectivesoft.bookservice.ui.client.CommentRestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.effectivesoft.bookservice.common.dto.CommentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class CommentComponent extends HorizontalLayout {
    private boolean isLiked;
    private int likesCount;
    private StringBuilder ratedUsersPreview;
    private Integer likesOnPage = 0;

    private static final Long MINUTE_IN_MILLISECONDS = (long) 60000;
    private static final Long HOUR_IN_MILLISECONDS = (long) 3600000;
    private static final Long DAY_IN_MILLISECONDS = (long) 86400000;
    private static final Long MONTH_IN_MILLISECONDS = (long) 1339200000;
    private static final Long YEAR_IN_MILLISECONDS = (long) 16070400000.0;

    private static final Logger logger = LoggerFactory.getLogger(UserComment.class);

    public CommentComponent(CommentRestClient commentRestClient, CommentDto commentDto, UserDto userDto) {
        isLiked = commentDto.isLiked();
        likesCount = commentDto.getLikesCount();
        ratedUsersPreview = new StringBuilder();

        setWidth("70%");

        VerticalLayout details = new VerticalLayout();
        details.setAlignItems(Alignment.CENTER);
        details.setJustifyContentMode(JustifyContentMode.CENTER);

        Image logo = new Image();
        logo.setSrc(commentDto.getUserMainImageLink());
        logo.setWidth("50px");
        logo.setHeight("50px");

        details.add(logo, new Label(commentDto.getUserFirstName() + " " + commentDto.getUserLastName()),
                new Text(dateAnalysis(commentDto.getDateAdded())));
        details.setWidth("22%");

        TextArea textArea = new TextArea();
        textArea.setValue(commentDto.getText());
        textArea.setReadOnly(true);
        textArea.setWidth("68%");


        HorizontalLayout likeLayout = new HorizontalLayout();
        likeLayout.setWidth("10%");
        likeLayout.setAlignItems(Alignment.CENTER);
        likeLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        LikesCountComponent likesCountLabel = new LikesCountComponent(String.valueOf(likesCount));

        likesCountLabel.setClassName("likes-count-label");

        Icon likeIcon = new Icon(VaadinIcon.HEART_O);

        likeIcon.setClassName("like-icon");

        if (isLiked) {
            likeIcon.setColor("#FF0000");
            likesCountLabel.getElement().setAttribute("style", "font-size: 20px; color: #FF0000;");
        }

        likeIcon.addClickListener(onClick -> {
            if (!isLiked) {
                try {
                    if (commentRestClient.likeBookComment(commentDto.getId())) {
                        likeIcon.setColor("#FF0000");
                        likesCountLabel.getElement().setAttribute("style", "font-size: 20px; color: #FF0000;");
                        likesCount++;
                        likesCountLabel.setText(String.valueOf(likesCount));
                        isLiked = true;
                        ratedUsersPreview.insert(0, "♥ " + userDto.getFirstName() + " " + userDto.getLastName() + "\n");
                        likesCountLabel.setClassName("tooltip");
                        likesCountLabel.getElement().setAttribute("data-tooltip", ratedUsersPreview.toString());
                    }
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage());
                }
            } else {
                try {
                    if (commentRestClient.dislikeBookComment(commentDto.getId())) {
                        likeIcon.setColor("#000000");
                        likesCountLabel.getElement().setAttribute("style", "font-size: 20px; color: #000000;");
                        likesCount--;
                        likesCountLabel.setText(String.valueOf(likesCount));
                        isLiked = false;
                        ratedUsersPreview = new StringBuilder(ratedUsersPreview.toString().replace("♥ " + userDto.getFirstName() + " " + userDto.getLastName() + "\n", ""));
                        likesCountLabel.getElement().setAttribute("data-tooltip", ratedUsersPreview.toString());
                        if (ratedUsersPreview.length() == 0) {
                            likesCountLabel.getElement().removeAttribute("class");
                        }
                    }
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage());
                }
            }
        });


        if (commentDto.getRatedUsers() != null && commentDto.getRatedUsers().size() != 0) {
            for (UserDto user : commentDto.getRatedUsers()) {
                ratedUsersPreview.append("♥ ").append(user.getFirstName()).append(" ").append(user.getLastName()).append("\n");
            }
            likesCountLabel.setClassName("tooltip");
            likesCountLabel.getElement().setAttribute("data-tooltip", ratedUsersPreview.toString());
        }

        likeLayout.add(likeIcon, likesCountLabel);
        likeLayout.getElement().removeAttribute("theme");

        Dialog dialog = new Dialog();
        dialog.setHeight("350px");

        HorizontalLayout dialogHeaderLayout = new HorizontalLayout();
        dialogHeaderLayout.setWidthFull();
        dialogHeaderLayout.setAlignItems(Alignment.CENTER);

        Label dialogHeaderLabel = new Label("Liked");

        Icon dialogCloseIcon = new Icon(VaadinIcon.CLOSE_CIRCLE_O);
        dialogCloseIcon.addClickListener(onClick -> {
            likesOnPage = 0;
            dialog.close();
        });
        dialogCloseIcon.setClassName("dialog-close-icon");

        dialogHeaderLayout.add(dialogHeaderLabel, dialogCloseIcon);

        VerticalLayout mainDialogLayout = new VerticalLayout();
        mainDialogLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        mainDialogLayout.setAlignItems(Alignment.CENTER);

        mainDialogLayout.add(dialogHeaderLayout);

        VerticalLayout likedUsersLayout = new VerticalLayout();
        likedUsersLayout.setHeight("300px");

        Button button = new Button("Show more");
        button.addClickListener(onClick -> {
            try {
                List<UserDto> users = commentRestClient.readBookCommentRatedUsers(commentDto.getId(), likesOnPage + 10, likesOnPage);
                for (UserDto user : users) {
                    likedUsersLayout.add(new RatedUserComponent(user.getPhotoLink(), user.getFirstName(), user.getLastName()));
                }
                likesOnPage += users.size();
                if (likesOnPage == likesCount) {
                    mainDialogLayout.remove(button);
                }

            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        });

        likesCountLabel.addClickListener(onClick -> {
            try {
                List<UserDto> users = commentRestClient.readBookCommentRatedUsers(commentDto.getId(), likesOnPage + 10, likesOnPage);
                for (UserDto user : users) {
                    likedUsersLayout.add(new RatedUserComponent(user.getPhotoLink(), user.getFirstName(), user.getLastName()));
                }
                mainDialogLayout.add(likedUsersLayout);
                dialog.add(mainDialogLayout);
                dialog.open();
                likesOnPage += users.size();
                if (likesOnPage < likesCount) {
                    mainDialogLayout.add(button);
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        });

        dialog.addDialogCloseActionListener(onClose -> {
            dialog.close();
            likesOnPage = 0;
            likedUsersLayout.removeAll();
        });

        dialogCloseIcon.addClickListener(onClick -> {
            dialog.close();
            likesOnPage = 0;
            likedUsersLayout.removeAll();
        });

        textArea.getElement()
                .removeAttribute("style");
        textArea.setClassName("text-area");

        add(details, textArea, likeLayout);
    }

    private String dateAnalysis(LocalDateTime commentDate) {
        Date now = new Date();
        Date date = Date.from(commentDate.atZone(ZoneId.systemDefault()).toInstant());

        long difference = now.getTime() - date.getTime();

        long count;

        if (difference <= MINUTE_IN_MILLISECONDS) {
            return "1 minute ago";
        }

        if (difference <= HOUR_IN_MILLISECONDS) {
            count = difference / MINUTE_IN_MILLISECONDS;
            if (count == 1) {
                return count + " minute ago";
            }
            return count + " minutes ago";
        }

        if (difference <= DAY_IN_MILLISECONDS) {
            count = difference / HOUR_IN_MILLISECONDS;
            if (count == 1) {
                return count + " hour ago";
            }
            return count + " hours ago";
        }

        if (difference <= MONTH_IN_MILLISECONDS) {
            count = difference / DAY_IN_MILLISECONDS;
            if (count == 1) {
                return count + " day ago";
            }
            return count + " days ago";
        }

        if (difference <= YEAR_IN_MILLISECONDS) {
            count = difference / MONTH_IN_MILLISECONDS;
            if (count == 1) {
                return count + " month ago";
            }
            return count + " months ago";
        }

        count = difference / YEAR_IN_MILLISECONDS;
        if (count == 1) {
            return count + " year ago";
        }

        return count + " years ago";
    }
}