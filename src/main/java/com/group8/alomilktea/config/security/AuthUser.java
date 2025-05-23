package com.group8.alomilktea.config.security;

import com.group8.alomilktea.entity.Roles;
import com.group8.alomilktea.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class AuthUser implements UserDetails {


    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;

    private String code;
    private boolean isEnabled;

    private Set<Roles> userRoles;

    public AuthUser(User user) {
        this.id = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.code = user.getCode();
        this.isEnabled= user.getIsEnabled();
        userRoles = user.getRoles();
        this.password = user.getPasswordHash();
        System.out.println("pw" + user.getPasswordHash());
    }

    public AuthUser(User user, String password) {
        this.id = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.code = user.getCode();
        this.isEnabled= user.getIsEnabled();
        userRoles = user.getRoles();
        this.password = password;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> auth = new ArrayList<>();
        for(Roles role: this.userRoles) {
            auth.add(new SimpleGrantedAuthority(role.getRole().getRoleName()));
        }
        return auth;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }



}
