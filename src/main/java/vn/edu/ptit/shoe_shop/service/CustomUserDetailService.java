package vn.edu.ptit.shoe_shop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.entity.User;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserService userService;

    private final Logger log = LoggerFactory.getLogger(CustomUserDetail.class);

    public CustomUserDetailService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userService.getUserByUsernameOrEmail(username);
        if (user == null) {
            log.error("User not found with username: {}", username);
            throw new UsernameNotFoundException("user not found with username: " + username);
        }
        return new CustomUserDetail(user);
    }

}
