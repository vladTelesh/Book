package com.effectivesoft.bookservice.core.dao.impl;

import com.effectivesoft.bookservice.core.dao.AbstractDao;
import com.effectivesoft.bookservice.core.dao.UserDao;
import com.effectivesoft.bookservice.core.model.User;
import com.effectivesoft.bookservice.core.model.enums.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class UserDaoImpl extends AbstractDao<User> implements UserDao {

    @PersistenceContext
    EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    protected Class<User> getEntityType() {
        return User.class;
    }

    @Override
    public Optional<User> read(String id) {
        return Optional.ofNullable((User) entityManager.createNativeQuery("SELECT * FROM user WHERE id = ?1 AND user_status = ?2", User.class)
                .setParameter(1, id)
                .setParameter(2, UserStatus.STATUS_ACTIVE.toString())
                .getSingleResult());
    }

    @Override
    public Optional<User> update(User user) {
        entityManager.createNativeQuery("UPDATE user SET first_name = ?1, last_name = ?2, date_of_birth = ?3 WHERE id = ?4")
                .setParameter(1, user.getFirstName())
                .setParameter(2, user.getLastName())
                .setParameter(3, user.getDateOfBirth())
                .setParameter(4, user.getId()).executeUpdate();
        return Optional.of(user);
    }

    @Override
    public void delete(String id) {
        User user = entityManager.find(getEntityType(), id);
        user.setUserStatus(UserStatus.STATUS_DELETED.toString());
        entityManager.merge(user);
    }

    public Optional<User> readByUsername(String username, boolean google) {
        try {
            return Optional.ofNullable((User) entityManager.createNativeQuery("SELECT * FROM user WHERE username = ?1 AND is_confirm = ?2 AND is_google = ?3", User.class)
                    .setParameter(1, username)
                    .setParameter(2, true)
                    .setParameter(3, google)
                    .getSingleResult());
        } catch (NoResultException e) {
            logger.warn(e.getMessage());
            logger.error("" + google);
            logger.error("" + username);
            return Optional.empty();
        }
    }

    public Optional<User> readByUsername(String username) {
        try {
            return Optional.ofNullable((User) entityManager.createNativeQuery("SELECT * FROM user WHERE username = ?1 AND is_confirm = ?2", User.class)
                    .setParameter(1, username)
                    .setParameter(2, true)
                    .getSingleResult());
        } catch (NoResultException e) {
            logger.warn(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean confirmAccount(String code) {
        try {
            User user = (User) entityManager.createNativeQuery("SELECT * FROM user WHERE is_confirm = ?1 AND confirmation_code = ?2", User.class)
                    .setParameter(1, false)
                    .setParameter(2, code)
                    .getSingleResult();
            user.setConfirmed(true);
            entityManager.merge(user);
            return true;
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public int updatePassword(String userId, String newPassword) {
        return entityManager.createNativeQuery("UPDATE user SET password_hash = ?1 WHERE id = ?2")
                .setParameter(1, newPassword)
                .setParameter(2, userId)
                .executeUpdate();
    }
}
