package com.mungwithme.user.model.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.mungwithme.address.model.entity.Address;
import com.mungwithme.pet.model.dto.response.PetInfoResponseDto;
import com.mungwithme.user.model.enums.Gender;
import com.mungwithme.user.model.enums.SocialType;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoResponseDto {

    private Long userId;

    private String email;           // 이메일(ID)
    private String nickname;        // 닉네임
    private Integer age;                // 나이(추가정보)
    private Gender gender;          // 성별
    private SocialType socialType;  // 소셜 채널(Kakao, Google, Naver)

    @Builder.Default
    private Set<Address> regions = new HashSet<>();


    private PetInfoResponseDto pet;

}
