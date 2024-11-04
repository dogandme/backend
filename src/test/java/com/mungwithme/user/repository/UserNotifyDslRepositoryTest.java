package com.mungwithme.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.model.entity.UserNotify;
import com.mungwithme.user.service.UserQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


@SpringBootTest
class UserNotifyDslRepositoryTest {

    @Autowired
    UserQueryService userQueryService;


    @Autowired
    UserNotifyDslRepository userNotifyDslRepository;


    @Test
    void findNotifyListByUser() {

        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);

        PageRequest pageRequest = PageRequest.of(0, 20);

        Page<UserNotify> notifyListByUser =
            userNotifyDslRepository.findNotifyListByUser(user, pageRequest);


    }
}