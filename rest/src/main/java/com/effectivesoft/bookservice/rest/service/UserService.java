package com.effectivesoft.bookservice.rest.service;

import com.effectivesoft.bookservice.core.dao.ImageDao;
import com.effectivesoft.bookservice.core.dao.UserDao;
import com.effectivesoft.bookservice.core.model.Image;
import com.effectivesoft.bookservice.core.model.User;
import com.effectivesoft.bookservice.core.model.enums.UserStatus;
import com.effectivesoft.bookservice.common.dto.PasswordsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Value("${ui.server}")
    String link;
    private final UserDao userDao;
    private final JavaMailSender javaMailSender;
    private final ImageDao imageDao;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    UserService(@Autowired UserDao userDao,
                @Autowired ImageDao imageDao,
                @Autowired JavaMailSender javaMailSender) {
        this.userDao = userDao;
        this.imageDao = imageDao;
        this.javaMailSender = javaMailSender;
    }

    @Transactional
    public Optional<User> createUser(User user) {
        user.setId(UUID.randomUUID().toString());
        user.setUserStatus(UserStatus.STATUS_ACTIVE.toString());
        user.setCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder().encode(user.getPassword()));
        user.setConfirmed(false);

        Optional<User> optionalUser = userDao.create(user);
        if (optionalUser.isPresent()) {
            MailSender mailSender = new MailSender(user.getUsername(), user.getCode(), link, javaMailSender);
            mailSender.run();
        } else {
            logger.debug("Unable to create user with username " + "\"" + user.getUsername() + "\"");
        }
        return optionalUser;
    }

    @Transactional
    public Optional<User> createGoogleUser(User user) {
        user.setId(UUID.randomUUID().toString());
        user.setUserStatus(UserStatus.STATUS_ACTIVE.toString());
        user.setConfirmed(true);
        user.setGoogle(true);

        Image image = new Image();
        image.setId(UUID.randomUUID().toString());
        image.setUserId(user.getId());
        image.setLink(user.getPhotoLink());
        image.setMain(true);

        if (imageDao.create(image).isPresent()) {
            return userDao.create(user);
        } else {
            return Optional.empty();
        }
    }


    public Optional<User> readUser(String id) {
        return userDao.read(id);
    }

    public Optional<User> readUserByUsername(String username, boolean google) {
        return userDao.readByUsername(username, google);
    }

    public Optional<User> readUserByUsername(String username) {
        return userDao.readByUsername(username);
    }

    @Transactional
    public Optional<User> updateUser(User user, String userId) {
        user.setId(userId);
        return userDao.update(user);
    }

    @Transactional
    public boolean updateUserPassword(String userId, String username, PasswordsDto passwords) {
        if (!passwords.getNewPassword().equals(passwords.getConfirmPassword())) {
            return false;
        }

        Optional<User> user = userDao.readByUsername(username, false);
        if (user.isEmpty()) {
            return false;
        }

        if (!passwordEncoder().matches(passwords.getCurrentPassword(), user.get().getPassword())) {
            return false;
        }


        int count = userDao.updatePassword(userId,
                passwordEncoder().encode(passwords.getNewPassword()));

        return count == 1;
    }

    @Transactional
    public void deleteUser(String id) {
        userDao.delete(id);
    }

    @Transactional
    public boolean confirmUser(String code) {
        return userDao.confirmAccount(code);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
