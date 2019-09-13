package com.effectivesoft.bookservice.core.dao;

import com.effectivesoft.bookservice.core.model.Image;

import java.util.List;
import java.util.Optional;

public interface ImageDao extends BaseDao<Image>{
    List<Image> readImages(String userId);

    void updateMainImage(String userId, String imageLink);

    Optional<Image> readMainImage(String userId);
}
