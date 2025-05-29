package com.group8.alomilktea.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication)
            throws IOException, ServletException {

        String username = (authentication != null) ? authentication.getName() : "Anonymous";
        String ipAddress = request.getRemoteAddr();
        LocalDateTime timestamp = LocalDateTime.now();

        logger.info("LOGOUT - username: {}, IP: {}, time: {}", username, ipAddress, timestamp);

        response.sendRedirect("/");
    }
}
