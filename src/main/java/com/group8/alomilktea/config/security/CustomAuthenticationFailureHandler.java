package com.group8.alomilktea.config.security;

import com.group8.alomilktea.entity.User;
import com.group8.alomilktea.service.IUserService;
import com.group8.alomilktea.utils.Constant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

    @Autowired
    private IUserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        String username = request.getParameter("username");
        String ipAddress = request.getRemoteAddr();
        LocalDateTime timestamp = LocalDateTime.now();

        logger.warn("LOGIN FAILURE - username: {}, IP: {}, time: {}, reason: {}",
                username, ipAddress, timestamp, exception.getMessage());

        if (username != null && !username.isEmpty()) {
            HttpSession session = request.getSession();
            String attemptSessionKey = "failedLoginAttempts_" + username;
            Integer attempts = (Integer) session.getAttribute(attemptSessionKey);
            if (attempts == null) {
                attempts = 0;
            }
            attempts++;

            User user = userService.getByUserNameOrEmail(username).orElse(null);

            if (user != null && !user.getIsEnabled()) {
                logger.warn("Login attempt for already locked account: {} from IP: {}", username, ipAddress);
                response.sendRedirect(request.getContextPath() + "/auth/login?message=locked");
                return;
            }

            if (attempts > Constant.MAX_LOGIN_FAILED_ATTEMPTS) {
                if (user != null) {
                    user.setIsEnabled(false);
                    userService.save(user);
                    logger.warn("User account {} locked due to {} failed login attempts.", username, Constant.MAX_LOGIN_FAILED_ATTEMPTS);
                    session.removeAttribute(attemptSessionKey);
                    response.sendRedirect(request.getContextPath() + "/auth/login?message=locked");
                } else {
                    logger.warn("Login attempt for non-existent user: {}", username);
                    response.sendRedirect(request.getContextPath() + "/auth/login?message=error");
                }
            } else {
                session.setAttribute(attemptSessionKey, attempts);
                response.sendRedirect(request.getContextPath() + "/auth/login?message=error&attempts=" + attempts);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/auth/login?message=error");
        }
    }
}