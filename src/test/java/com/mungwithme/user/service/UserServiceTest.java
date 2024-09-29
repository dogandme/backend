package com.mungwithme.user.service;

import com.mungwithme.user.model.dto.request.UserAddressUpdateDto;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserQueryService userQueryService;

    @Autowired
    UserService userService;

    @Test
    void getCurrentUser_V2() {
        userQueryService.findCurrentUser_v2();
    }


    @Test
    public void removeUser() {

        // given

        // when

        // then

//        userService.removeUser();

    }


    @Test
    public void editAddress() {

        // given
        Set<Long> removeIds = new HashSet<>();
        removeIds.add(17L);
        removeIds.add(19L);
        removeIds.add(20L);
        removeIds.add(10L);
        removeIds.add(15L);

        Set<Long> addIds = new HashSet<>();
//        addIds.add(17L);
//        addIds.add(19L);
//        addIds.add(20L);
//        addIds.add(10L);
//        addIds.add(15L);

//        UserAddressUpdateDto userAddressUpdateDto = new UserAddressUpdateDto(removeIds, addIds);
//         when
//        userService.editAddress(userAddressUpdateDto);

        // then

    }
}