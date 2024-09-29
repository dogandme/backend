package com.mungwithme.profile.model.dto.response;

import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.pet.model.dto.response.PetInfoResponseDto;
import com.mungwithme.user.model.SocialType;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProfileResponseDto {

    private String nickname;                        // 닉네임
    private SocialType socialType;                  // 소셜 로그인 타입
    private int tempCnt;                            // 임시 저장 수
    private PetInfoResponseDto pet;                 // 펫 정보
    private List<Long> followers;                   // 팔로워 userId 목록
    private List<Long> Followings;                  // 팔로윙 userId 목록
    private List<Long> likes;                       // 좋아요 마킹 id 목록
    private List<Map<String, Object>> markings;     // 마킹 id, 썸네일 목록

}
