package com.mungwithme.user.controller;

import static org.junit.jupiter.api.Assertions.*;

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
}