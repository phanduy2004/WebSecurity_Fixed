package com.group8.alomilktea.config.security;

import com.group8.alomilktea.common.enums.UserRole;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {



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
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(this.userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        final List<GlobalAuthenticationConfigurerAdapter> configurerAdapters = new ArrayList<>();
        configurerAdapters.add(new GlobalAuthenticationConfigurerAdapter() {
            @Override
            public void configure(AuthenticationManagerBuilder auth) throws Exception {
                super.configure(auth);
            }
        });
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    	httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
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
                .defaultSuccessUrl("/")
                .failureHandler(new AuthenticationFailureHandler() {
					@Override
					public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
							AuthenticationException exception)
							throws IOException, ServletException {
						request.getSession().setAttribute("loginStatus", "failure");
                        response.sendRedirect("/auth/login?message=error");				
					}
                }).permitAll())
        .rememberMe(re -> re.key("uniqueAndSecret")
                .rememberMeCookieName("tracker-remember-me")
                .userDetailsService(userDetailsService())
                .tokenValiditySeconds(5000)
        )
        .logout(l -> l.invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .permitAll()
        )
        .exceptionHandling(e -> e.accessDeniedPage("/403"))
        .sessionManagement(session -> session
        .maximumSessions(1)
        .expiredUrl("/")
        .maxSessionsPreventsLogin(true)) ;


return httpSecurity.build();
    }
}
