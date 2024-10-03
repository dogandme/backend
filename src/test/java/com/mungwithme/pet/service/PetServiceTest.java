package com.mungwithme.pet.service;

import static org.junit.jupiter.api.Assertions.*;

import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PetServiceTest {


    @Autowired
    UserQueryService userQueryService;

    @Autowired
    PetService petService;

    @Test
    void deletePet() {
        User user = userQueryService.findByEmail("mungWithMe6@gmail.com").orElse(null);
//
//        petService.deletePet(user);

        petService.deletePet(user);

    }
}