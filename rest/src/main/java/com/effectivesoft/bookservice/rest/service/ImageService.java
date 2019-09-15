package com.effectivesoft.bookservice.rest.service;

import com.effectivesoft.bookservice.core.dao.ImageDao;
import com.effectivesoft.bookservice.core.model.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${image.directory}")
    private String directory;
    @Value("${server.port}")
    private String port;
    @Value("${server.host}")
    private String host;
    @Value("${hosting.images.url}")
    private String imagesHostingUrl;

    private final ImageDao imageDao;

    ImageService(@Autowired ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    @Transactional
    public Optional<Image> createImage(MultipartFile imageFile, String userId, String isMain) throws IOException {
        String path = directory + "/user/" + userId;
        File folder = new File(path);
        folder.mkdir();
        Image image = new Image();
        image.setId(UUID.randomUUID().toString());
        image.setLink("http://" + host + ":" + port + imagesHostingUrl + "/user/" +  userId + "/" + image.getId() + ".jpg");
        image.setUserId(userId);
        image.setMain(Boolean.parseBoolean(isMain));
        imageFile.transferTo(new File(folder.getPath() + "\\" + image.getId() + ".jpg"));
        return imageDao.create(image);
    }

    public Optional<Image> readMainImage(String userId) {
        return imageDao.readMainImage(userId);
    }

    public List<Image> readImages(String userId) {
        return imageDao.readImages(userId);
    }

    @Transactional
    public void updateMainImage(String userId, String imageLink) {
         imageDao.updateMainImage(userId, imageLink);
    }

    @Transactional
    public void deleteImage(String imageId) {
        imageDao.delete(imageId);
    }
}
