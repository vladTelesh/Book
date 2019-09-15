package com.effectivesoft.bookservice.core.dao.impl;

import com.effectivesoft.bookservice.core.dao.AbstractDao;
import com.effectivesoft.bookservice.core.dao.UserGoalDao;
import com.effectivesoft.bookservice.core.model.UserGoal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class UserGoalDaoImpl extends AbstractDao<UserGoal> implements UserGoalDao {

    @PersistenceContext
    EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(UserGoalDaoImpl.class);

    @Override
    protected Class<UserGoal> getEntityType() {
        return UserGoal.class;
    }

    @Override
    public Optional<UserGoal> read(String userId) {
        try {
            return Optional.of((UserGoal) entityManager.createNativeQuery("SELECT * FROM user_goal " +
                    "WHERE user_id = ?1", UserGoal.class)
                    .setParameter(1, userId)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
