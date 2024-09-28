package com.mungwithme.user.model.dto.request;


import com.mungwithme.common.annotation.valid.PwValid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class UserPwUpdateDto {



    @PwValid
    private String password;

    @PwValid
    private String newPw;

    @PwValid
    private String newPwChk;

}
