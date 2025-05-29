package com.group8.alomilktea.config.security;

import com.group8.alomilktea.common.enums.UserRole;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
// Dòng này không cần thiết nếu bạn khởi tạo trực tiếp và chúng không phải là bean
// import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
// import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    @Autowired
    private AuthenticationFailureHandler customAuthenticationFailureHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new AuthHelper();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasAuthority(UserRole.ADMIN.getRoleName())
                        .requestMatchers("/manager/**").hasAuthority(UserRole.MANAGER.getRoleName())
                        .requestMatchers("/shipper/**").hasAuthority(UserRole.SHIPPER.getRoleName())
                        .requestMatchers("/cart/**", "/CheckOut/**", "/web/users/**", "/web/product/add-to-cart/**")
                        .hasAnyAuthority(UserRole.USER.getRoleName(), UserRole.ADMIN.getRoleName())
                        .requestMatchers("/auth/**", "/api/**", "/ws/**").permitAll() // Cho phép truy cập WebSocket
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .successHandler(new CustomAuthenticationSuccessHandler())
                        .failureHandler(customAuthenticationFailureHandler) // Sử dụng bean đã inject
                        .permitAll()
                )
                .rememberMe(rm -> rm
                        .key("uniqueAndSecret")
                        .rememberMeCookieName("tracker-remember-me")
                        .tokenValiditySeconds(5000)
                        .userDetailsService(userDetailsService())
                )
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                        .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                        .deleteCookies("JSESSIONID", "tracker-remember-me")
                        .permitAll()
                )
                .exceptionHandling(e -> e
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // Luôn trả về 403
                            response.getWriter().write("Access Denied!");
                        })
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true) // Ngăn chặn đăng nhập mới nếu đã đạt max session
                        .expiredUrl("/") // Chuyển hướng khi session hết hạn do đăng nhập từ nơi khác
                );

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}