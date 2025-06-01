package com.group8.alomilktea.config.security;

import com.group8.alomilktea.common.enums.UserRole;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

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
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                //.csrf(AbstractHttpConfigurer::disable)
                .csrf(Customizer.withDefaults()) // Bật csrf
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(antMatcher("/admin/**")).hasAnyAuthority(UserRole.ADMIN.getRoleName())
                                .requestMatchers(antMatcher("/manager/**")).hasAnyAuthority(UserRole.MANAGER.getRoleName())
                                .requestMatchers(antMatcher("/api/**")).permitAll()
                                .requestMatchers(antMatcher("/auth/**")).permitAll()
                                .requestMatchers(antMatcher("/cart/**")).hasAnyAuthority(UserRole.USER.getRoleName(), UserRole.ADMIN.getRoleName())
                                .requestMatchers(antMatcher("/CheckOut/**")).hasAnyAuthority(UserRole.USER.getRoleName(), UserRole.ADMIN.getRoleName())
                                .requestMatchers(antMatcher("/web/users/**")).hasAnyAuthority(UserRole.USER.getRoleName(), UserRole.ADMIN.getRoleName())
                                .requestMatchers(antMatcher("/web/product/add-to-cart/**")).hasAnyAuthority(UserRole.USER.getRoleName(), UserRole.ADMIN.getRoleName())
                                .requestMatchers(antMatcher("/**")).permitAll()
                                .requestMatchers(antMatcher("/shipper/**")).hasAnyAuthority(UserRole.SHIPPER.getRoleName())
                                .anyRequest().authenticated()
                ).formLogin(login -> login
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
                        .tokenValiditySeconds(5000)
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
                        .expiredUrl("/")
                        .maxSessionsPreventsLogin(true)
                )
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self'; object-src 'none'; frame-ancestors 'none'; form-action 'self';")
                        )
                );

        return httpSecurity.build();
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

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("https://localhost:8888"); // Khớp với server.port
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}