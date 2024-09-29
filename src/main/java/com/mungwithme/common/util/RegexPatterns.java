package com.mungwithme.common.util;

/**
 * 정규표현식 값을 모아둔 class
 *
 */
public class RegexPatterns {


    // 영문, 숫자, 특수기호를 조합한 8자리 이상 15자리 이내의 문자열
    public static final String PW_REGEX =  "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,15}$";

    // 최대 20자 이내 한글과 영어로만 이루어져야 함
    public static final String NICK_NAME_REGEX =  "^[가-힣a-zA-Z]{1,20}$";

}
