package com.mungwithme.common.util;

/**
 * 정규표현식 값을 모아둔 class
 *
 */
public class RegexPatterns {


    // 영문, 숫자, 특수기호를 조합한 8자리 이상 15자리 이내의 문자열
    public static final String PW_REGEX =  "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,15}$";

}
