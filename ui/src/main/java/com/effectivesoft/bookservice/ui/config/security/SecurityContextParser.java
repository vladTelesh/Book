package com.effectivesoft.bookservice.ui.config.security;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class SecurityContextParser {
    public static String getEmail() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails) {
            return ((UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getUsername();
        } else {
            return (String) ((OidcUser) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getClaims().get("email");
        }
    }
}
