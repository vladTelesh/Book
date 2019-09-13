package com.effectivesoft.bookservice.core.dao.impl;

import com.effectivesoft.bookservice.core.dao.AbstractDao;
import com.effectivesoft.bookservice.core.dao.ImageDao;
import com.effectivesoft.bookservice.core.model.Image;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class ImageDaoImpl extends AbstractDao<Image> implements ImageDao {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    protected Class<Image> getEntityType() {
        return Image.class;
    }


    @Override
    public List<Image> readImages(String userId) {
        return entityManager.createNativeQuery("SELECT * FROM image WHERE user_id = ?1", Image.class)
                .setParameter(1, userId)
                .getResultList();
    }

    @Override
    public void updateMainImage(String userId, String imageLink) {
       int i = entityManager.createNativeQuery(("UPDATE image SET is_main = false WHERE user_id = ?1"))
                .setParameter(1, userId)
                .executeUpdate() +
                entityManager.createNativeQuery("UPDATE image SET is_main = true WHERE user_id = ?1 AND link = ?2")
                        .setParameter(1, userId)
                        .setParameter(2, imageLink)
                        .executeUpdate() +
                entityManager.createNativeQuery("UPDATE user SET photo_link = ?1 WHERE id = ?2")
                        .setParameter(1, imageLink)
                        .setParameter(2, userId)
                        .executeUpdate();
    }

    @Override
    public Optional<Image> readMainImage(String userId) {
        return Optional.ofNullable((Image) entityManager.createNativeQuery("SELECT * FROM image WHERE user_id = ?1 AND is_main = true", Image.class)
                .setParameter(1, userId).getSingleResult());
    }
}
