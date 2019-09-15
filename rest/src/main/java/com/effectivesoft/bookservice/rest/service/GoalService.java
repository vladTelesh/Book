package com.effectivesoft.bookservice.rest.service;

import com.effectivesoft.bookservice.core.dao.UserGoalDao;
import com.effectivesoft.bookservice.core.model.UserGoal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class GoalService {
    private final UserGoalDao userGoalDao;

    private static final Logger logger = LoggerFactory.getLogger(GoalService.class);

    public GoalService(@Autowired UserGoalDao userGoalDao) {
        this.userGoalDao = userGoalDao;
    }

    @Transactional
    public Optional<UserGoal> createUserGoal(UserGoal userGoal, String userId) {
        userGoal.setUserId(userId);
        userGoal.setYear(LocalDate.now().getYear());

        logger.error(userGoal.getUserId() + " " + userGoal.getBookCount() + " " + userGoal.getYear());

        return userGoalDao.create(userGoal);
    }

    public Optional<UserGoal> readUserGoal(String userId) {
        return userGoalDao.read(userId);
    }

    @Transactional
    public Optional<UserGoal> updateUserGoal(UserGoal userGoal){
        return userGoalDao.update(userGoal);
    }
}
