package vn.edu.ptit.shoe_shop.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.edu.ptit.shoe_shop.common.enums.StatusEnum;
import vn.edu.ptit.shoe_shop.entity.User;

import java.util.Collection;
import java.util.List;

public class CustomUserDetail implements UserDetails {

    private final User user;
    private final List<SimpleGrantedAuthority> authorities;

    public CustomUserDetail(User user) {
        this.user = user;
        String roleCode = user.getRole() != null ? user.getRole().getCode() : "USER";
        if (!roleCode.startsWith("ROLE_")) {
            roleCode = "ROLE_" + roleCode;
        }
        this.authorities = List.of(new SimpleGrantedAuthority(roleCode));
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       return this.authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail() != null ? user.getEmail() : user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() != null && user.getStatus().equals(StatusEnum.ACTIVE);
    }
}
