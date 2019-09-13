package com.effectivesoft.bookservice.ui.config.security.impl;


import com.effectivesoft.bookservice.ui.client.UserRestClient;
import com.effectivesoft.bookservice.common.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRestClient userRestClient;

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    UserDetailsServiceImpl(@Autowired UserRestClient userRestClient) {
        this.userRestClient = userRestClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<UserDto> user;
        try {
            user = userRestClient.loadUserByUsername(username);
        } catch (IOException e) {
            logger.warn(e.getMessage());
            throw new NoSuchElementException(e.getMessage());
        }

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User " + username + " wasn't found in the database");
        }

        List<String> roleNames = new ArrayList<>();
        roleNames.add("ADMIN");

        List<GrantedAuthority> grantList = new ArrayList<>();
        for (String role : roleNames) {
            GrantedAuthority authority = new SimpleGrantedAuthority(role);
            grantList.add(authority);
        }

        return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), grantList);
    }
}
