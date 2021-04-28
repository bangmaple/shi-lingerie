package com.bangmaple.webflux.services;

import com.bangmaple.webflux.entities.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class UserPrincipal extends Users implements UserDetails {
    private static final long serialVersionUID = -6572014278512709432L;


    public UserPrincipal(Users user) {
        super(user);
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return new LinkedList<GrantedAuthority>(Collections
                .singleton(new SimpleGrantedAuthority(this.getRole())));
      //  return getRoles().stream()
          //      .map(role -> new SimpleGrantedAuthority(role
          //              .getName().name())).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.getUsername();
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
