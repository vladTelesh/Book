package com.effectivesoft.bookservice.ui.component;

import com.effectivesoft.bookservice.ui.client.UserRestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.effectivesoft.bookservice.common.dto.ImageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImagePicker extends HorizontalLayout {

    private final UserRestClient userRestClient;
    private static final Logger logger = LoggerFactory.getLogger(ImagePicker.class);
    private final int VISIBLE_IMAGES_COUNT = 3;
    private HorizontalLayout images;
    private List<Image> imagesList;

    private int current = 0;

    public ImagePicker(@Autowired UserRestClient userRestClient, Image mainImage) throws IOException {
        this.userRestClient = userRestClient;
        this.images = new HorizontalLayout();
        setWidthFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        imagesList = getUserImages(mainImage);

        Button left = new Button(new Icon(VaadinIcon.ARROW_LEFT));
        left.setEnabled(false);
        Button right = new Button(new Icon(VaadinIcon.ARROW_RIGHT));

        images.setAlignItems(Alignment.CENTER);
        images.setJustifyContentMode(JustifyContentMode.CENTER);

        left.addClickListener(onClick -> {
            current--;
            if (!right.isEnabled()) {
                right.setEnabled(true);
            }
            if (current == 0) {
                left.setEnabled(false);
            }

            images.removeAll();
            for (int i = current; i < current + 3; i++) {
                images.add(imagesList.get(i));
            }
        });
        right.addClickListener(onClick -> {
            current++;
            if (!left.isEnabled()) {
                left.setEnabled(true);
            }
            if (current == imagesList.size() - 3) {
                right.setEnabled(false);
            }

            images.removeAll();
            for (int i = current; i < current + 3; i++) {
                images.add(imagesList.get(i));
            }
        });


        if(imagesList.size() < VISIBLE_IMAGES_COUNT){
            for(int i = current; i < imagesList.size(); i++){
                images.add(imagesList.get(i));
            }
            right.setEnabled(false);
        } else {
            for(int i = current; i < VISIBLE_IMAGES_COUNT; i++){
                images.add(imagesList.get(i));
            }
        }


        add(left, images, right);
    }


    private List<Image> getUserImages(Image mainImage) throws IOException {
        List<Image> images = new ArrayList<>();
        List<ImageDto> imagesDto = userRestClient.readUserImages();

        for (ImageDto imageDto : imagesDto) {
            Image image = new Image(imageDto.getLink(), "");
            image.setWidth("60px");
            image.addClickListener(onClick -> {
                mainImage.setSrc(image.getSrc());
                try {
                    if(userRestClient.updateUserMainImage(imageDto)){
                        mainImage.setSrc(image.getSrc());
                    }
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage());
                }
            });
            images.add(image);
        }

        return images;
    }

    public void refreshImages(Image mainImage) throws IOException {
        this.imagesList = getUserImages(mainImage);
        this.images.removeAll();
        if(imagesList.size() < VISIBLE_IMAGES_COUNT){
            for(int i = current; i < imagesList.size(); i++){
                images.add(imagesList.get(i));
            }
        } else {
            for(int i = current; i < VISIBLE_IMAGES_COUNT; i++){
                images.add(imagesList.get(i));
            }
        }
    }
}

