package com.effectivesoft.bookservice.ui.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@SuppressWarnings("unchecked")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityUserService userDetailsService;

    SecurityConfig(@Autowired SecurityUserService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private static final String LOGIN_URL = "/sign_in";
    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_SUCCESS_URL = "/books";
    private static final String LOGIN_FAILURE_URL = "/sign_in";
    private static final String LOGOUT_SUCCESS_URL = "/sign_in";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable().requestCache().requestCache(new SecurityCustomRequestCache())
                .and()
                .antMatcher("/**")
                .authorizeRequests().requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()
                .antMatchers("/oauth2/**", "/sign_in", "/sign_up", "/login**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage(LOGIN_URL).permitAll()
                .loginProcessingUrl(LOGIN_PROCESSING_URL).successForwardUrl(LOGIN_SUCCESS_URL)
                .failureUrl(LOGIN_FAILURE_URL)
                .and()
                .logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL)
                .and()
                .oauth2Login().defaultSuccessUrl("/books", true)
                .userInfoEndpoint().oidcUserService(userDetailsService);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                "/VAADIN/**",
                "/favicon.ico",
                "/robots.txt",
                "/manifest.webmanifest",
                "/sw.js",
                "/offline-page.html",
                "/frontend/**",
                "/webjars/**",
                "/frontend-es5/**",
                "/frontend-es6/**");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PrincipalExtractor principalExtractor(SecurityUserService userDetailsService) {
        return map -> {

            List<String> roleNames = new ArrayList<>();
            roleNames.add("ADMIN");

            List<GrantedAuthority> grantList = new ArrayList<>();
            for (String role : roleNames) {
                GrantedAuthority authority = new SimpleGrantedAuthority(role);
                grantList.add(authority);
            }

            return new org.springframework.security.core.userdetails.User((String) map.get("email"), "", grantList);
        };
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
    }

    private ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("google")
                .clientId("1028850966185-qo8t0dgcv03rp3o0auich1pmqt7vmofb.apps.googleusercontent.com")
                .clientSecret("kIm8gQk8Am3hV0JVZLpGQZuZ")
                .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile", "email", "address", "phone")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .clientName("Google")
                .build();
    }
}