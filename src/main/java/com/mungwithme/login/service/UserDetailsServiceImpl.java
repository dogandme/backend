package com.mungwithme.login.service;

import com.mungwithme.security.oauth.dto.CustomUserDetails;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.repository.UserRepository;
import com.mungwithme.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserQueryService userQueryService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userQueryService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("error.notfound.email"));

        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), user.getRole());
    }
}
