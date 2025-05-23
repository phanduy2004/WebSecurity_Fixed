package com.group8.alomilktea.config.security;

import com.group8.alomilktea.entity.Roles;
import com.group8.alomilktea.entity.User;
import com.group8.alomilktea.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthHelper implements UserDetailsService {

    @Autowired
    private IUserService userService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userService.getByUserNameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("NOT FOUND" + username));

//
        if (user.getIsEnabled()) {
            return new AuthUser(user);
        }
        else
        {
            throw new DisabledException("User is not enabled");
        }

//

    }

    private Collection<? extends GrantedAuthority> mapRoleToAuth(Set<Roles> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRole().getRoleName())).collect(Collectors.toList());
    }
}
