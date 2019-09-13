package com.effectivesoft.bookservice.rest.filter;

import com.effectivesoft.bookservice.core.model.User;
import com.effectivesoft.bookservice.rest.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@WebFilter(urlPatterns = "/**")
public class AuthorizationFilter implements Filter {

    private final UserService userService;

    private static final List<String> PERMITTED_URL = Arrays.asList(
            "/api/v1/users/login",
            "/api/v1/users"
    );

    public AuthorizationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURL = ((HttpServletRequest) request).getRequestURI();

        if ((PERMITTED_URL.contains(requestURL) && ((HttpServletRequest) request).getMethod().equals("POST")) || ((HttpServletRequest) request).getMethod().equals("OPTIONS")
                || requestURL.contains("/api/v1/users/confirm/") || requestURL.contains("api/v1/hosting/")) {
            chain.doFilter(request, response);
            return;
        }

        if (httpRequest.getHeader("Username") == null) {
            ((HttpServletResponse) response).sendError(HttpStatus.UNAUTHORIZED.value());
        } else {
            Optional<User> user = userService.readUserByUsername(httpRequest.getHeader("Username"));
            if (user.isEmpty()) {
                ((HttpServletResponse) response).sendError(HttpStatus.UNAUTHORIZED.value());
            } else {
                ThreadLocal<UserLocalData> data = new ThreadLocal<>();
                data.set(new UserLocalData(user.get().getId(), user.get().getUsername()));
                ThreadLocalData.setData(data);
                chain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {

    }
}
