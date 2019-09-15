package com.effectivesoft.bookservice.core.dao;

import com.effectivesoft.bookservice.core.model.User;

import java.util.Optional;

public interface UserDao extends BaseDao<User> {
    Optional<User> readByUsername(String username, boolean google);

    Optional<User> readByUsername(String username);

    boolean confirmAccount(String code);

    int updatePassword(String userId, String newPassword);
}
