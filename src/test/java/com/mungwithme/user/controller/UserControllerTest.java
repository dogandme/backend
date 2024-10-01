package com.mungwithme.user.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class UserControllerTest {


    @Test
    public void test() {

        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,15}$";

        String password = "임하늘";

        boolean isValid = Pattern.matches(regex, password);

        if (isValid) {
            System.out.println("Password is valid");
        } else {
            System.out.println("Password is invalid");
        }
    }


    @Test
    public void test1() {

        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        System.out.println("ZonedDateTime = " + zonedDateTime);

        // given
        LocalDateTime now = LocalDateTime.now();
        System.out.println("now = " + now);
        // when

        // then

    }
}