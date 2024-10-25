package com.mungwithme.profile.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.mungwithme.pet.model.dto.response.PetInfoResponseDto;
import com.mungwithme.user.model.enums.SocialType;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProfileResponseDto {

    private String nickname;                        // 닉네임
    private int tempCnt;                            // 임시 저장 수
    private PetInfoResponseDto pet;                 // 펫 정보
    private List<Long> followersIds;                   // 팔로워 userId 목록
    private List<Long> FollowingsIds;                  // 팔로윙 userId 목록
    private SocialType socialType;                  // 소셜 로그인 타입



    // 타 유저 프로필 요청인 경우
    // key값 자체를 포함시키지 않음
    @JsonInclude(Include.NON_NULL)
    private List<Long> likes;                       // 좋아요 마킹 id 목록
    @JsonInclude(Include.NON_NULL)
    private List<Long> bookmarks;                   // 북마크 마킹 id 목록
    @JsonInclude(Include.NON_NULL)
    private List<Long> markings;     // 마킹 id, 썸네일 목록


}
