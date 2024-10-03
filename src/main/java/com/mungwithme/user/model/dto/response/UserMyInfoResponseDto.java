package com.mungwithme.user.model.dto.response;


import com.mungwithme.address.model.dto.response.AddressResponseDto;
import com.mungwithme.user.model.enums.Gender;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
public class UserMyInfoResponseDto {

    private String nickname;
    private Gender gender;
    private int age;
    private List<AddressResponseDto> regions;

}
