package com.mungwithme.user.model.dto.request;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
public class UserDeleteDto {
    private String password;

}
