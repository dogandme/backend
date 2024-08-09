package com.mungwithme.security.oauth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Iterator;


/**
 *
 * 로그인한 사용자의 유니크한 식별자를 볼 수 있게 해놓은 화면
 * ex) id, role
 * 비로그인 상태여도 접근가능 (annonymous)
 *
 */
@Controller
public class MainController {

    @GetMapping("/")
    @ResponseBody
    public String mainAPI() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        return "main COntroller\n" + username + "====== " + role;
    }
}