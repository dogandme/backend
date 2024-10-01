package com.mungwithme.user.model.dto.sql;


import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.user.model.entity.UserFollows;
import lombok.Getter;

@Getter
public class UserFollowQueryDto {

    private UserFollows userFollows;
    private Pet pet;

    public UserFollowQueryDto(UserFollows userFollows, Pet pet) {
        this.userFollows = userFollows;
        this.pet = pet;
    }
}
