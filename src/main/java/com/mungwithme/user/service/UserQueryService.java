package com.mungwithme.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {


/*
    public User findOne() {
        Authentication authentication = getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();


        return
    }

    private static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority
        ).findFirst().orElse(null);
        if (authorities == null) {
            throw new IllegalArgumentException("ex) user notFind");
        }
        return authentication;
    }*/
}
