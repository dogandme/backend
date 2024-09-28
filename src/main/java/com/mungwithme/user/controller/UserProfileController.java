package com.mungwithme.user.controller;


import com.mungwithme.common.annotation.authorize.UserAuthorize;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.user.model.dto.request.UserPwUpdateDto;
import com.mungwithme.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/profile")
public class UserProfileController {

    private final UserService userService;
    private final BaseResponse baseResponse;


    /**
     * password 업데이트 API
     *
     * 임시비밀번호를 보내는 url 과 겹쳐서
     * 권한을 어노테이션으로 등록
     *
     * @param userPwUpdateDto
     * @return
     */
    @PutMapping("/password")
    public ResponseEntity<CommonBaseResult> updatePassword(@RequestBody @Validated UserPwUpdateDto userPwUpdateDto,
        HttpServletRequest request)
        throws IOException {
        userService.editPassword(userPwUpdateDto);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(),"pw.modify.success",request.getLocale());
    }


}
