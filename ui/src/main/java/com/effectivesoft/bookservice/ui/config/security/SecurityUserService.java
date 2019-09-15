package com.effectivesoft.bookservice.ui.config.security;

import com.effectivesoft.bookservice.common.dto.GoogleUserDto;
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
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SecurityUserService implements UserDetailsService, OAuth2UserService {

    private final UserRestClient userRestClient;

    private static final Logger logger = LoggerFactory.getLogger(SecurityUserService.class);

    SecurityUserService(@Autowired UserRestClient userRestClient) {
        this.userRestClient = userRestClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<UserDto> user;
        try {
            user = userRestClient.readUser(username, false);
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

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        List<GrantedAuthority> grantList = new ArrayList<>();
        grantList.add(new SimpleGrantedAuthority("ADMIN"));

        try {
            Optional<UserDto> user = userRestClient.readUser((String) ((OidcUserRequest) userRequest)
                    .getIdToken().getClaims().get("email"), true);

            if (user.isPresent()) {
                return new DefaultOidcUser(grantList, ((OidcUserRequest) userRequest).getIdToken());
            } else {
                GoogleUserDto newUser = new GoogleUserDto();
                newUser.setUsername((String) ((OidcUserRequest) userRequest).getIdToken().getClaims().get("email"));
                newUser.setName((String) ((OidcUserRequest) userRequest).getIdToken().getClaims().get("name"));
                newUser.setPhotoLink((String) ((OidcUserRequest) userRequest).getIdToken().getClaims().get("picture"));

                if (userRestClient.createGoogleUser(newUser)) {
                    return new DefaultOidcUser(grantList, ((OidcUserRequest) userRequest).getIdToken());
                } else {
                    logger.error("-");
                    return null;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }

    }
}
