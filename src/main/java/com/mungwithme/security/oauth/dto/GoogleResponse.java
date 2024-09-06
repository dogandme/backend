package com.mungwithme.security.oauth.dto;

import java.util.Map;

public class GoogleResponse extends OAuth2Response{

    public GoogleResponse(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
