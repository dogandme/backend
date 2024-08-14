package com.mungwithme.security.oauth.dto;


import com.mungwithme.user.model.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final User user;

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return user.getRole().toString();
            }
        });

        return collection;
    }

    @Override
    public String getName() {
        return user.getNickname();
    }

    public String getEmail(){
        return user.getEmail();
    }

    public String getUsername() {

        return user.getNickname();
    }
}
