package com.mungwithme.security.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * /my경로는 로그인한 사용자만 접근할 수 있도록 securityConfig설정
 * 로그인이 제대로 되었는지 확인하는 용도.
 */

@Controller
public class MyController {

    @GetMapping("/my")
    @ResponseBody
    public String myAPI() {

        return "my route";
    }
}