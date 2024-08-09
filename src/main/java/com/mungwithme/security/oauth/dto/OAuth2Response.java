package com.mungwithme.security.oauth.dto;



/**
 * Oauth2 정보를 가져오기 위한 공용 인터페이스 : 이유 -> 네이버와 구글 등 소셜로그인마다 정보를 보내주는 방식이 다름
 * { Naver
 * 		resultcode=00, message=success, response={id=123123123, name=초록이}
 * }
 * {
 * 		resultcode=00, message=success, id=123123123, name=빨강과노랑이
 * }
 *
 */
public interface OAuth2Response {

    //제공자 (Ex. naver, google, ...)
    String getProvider();
    //제공자에서 발급해주는 아이디(번호)
    String getProviderId();
    //이메일
    String getEmail();
    //사용자 실명 (설정한 이름)
    String getName();
}
