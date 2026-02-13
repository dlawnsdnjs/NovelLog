package com.example.novelcharacter.service;

import com.example.novelcharacter.domain.User.CustomUserDetails;
import com.example.novelcharacter.domain.User.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userData = userService.getUserById(username);

        if (userData != null) {
            return new CustomUserDetails(userData);
        }

        throw new UsernameNotFoundException(username + " not found");
    }
}
