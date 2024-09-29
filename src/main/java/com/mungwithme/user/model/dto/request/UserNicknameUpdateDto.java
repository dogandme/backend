package com.mungwithme.user.model.dto.request;


import com.mungwithme.common.annotation.valid.NicknameValid;
import com.mungwithme.common.annotation.valid.PwValid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class UserNicknameUpdateDto {
    @NicknameValid
    private String nickname;
}
