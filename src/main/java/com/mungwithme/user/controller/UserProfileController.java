package com.mungwithme.user.controller;


import com.mungwithme.common.annotation.authorize.UserAuthorize;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.user.model.dto.request.*;
import com.mungwithme.user.model.dto.response.UserMyInfoResponseDto;
import com.mungwithme.user.service.UserQueryService;
import com.mungwithme.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    private final UserQueryService userQueryService;
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


    /**
     *
     * 내정보 수정 페이지 진입시 필요한 정보를 가져오는 API
     * @return
     * @throws IOException
     */
    @GetMapping
    public ResponseEntity<CommonBaseResult> getMyInfo() throws IOException {
        return baseResponse.sendContentResponse(userQueryService.findMyInfo(),HttpStatus.OK.value());
    }


    /**
     * nickname 업데이트 API
     *
     * 임시비밀번호를 보내는 url 과 겹쳐서
     * 권한을 어노테이션으로 등록
     *
     * @param userNicknameUpdateDto
     * @return
     */
    @PutMapping("/nickname")
    public ResponseEntity<CommonBaseResult> updateNickname(@RequestBody @Validated UserNicknameUpdateDto userNicknameUpdateDto,
        HttpServletRequest request)
        throws IOException {
        userService.editNickname(userNicknameUpdateDto);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(),"nickname.modify.success",request.getLocale());
    }



    /**
     * 성별 업데이트 API
     *
     *
     * @param userGenderUpdateDto
     * @param request
     * @return
     * @throws IOException
     */
    @PutMapping("/gender")
    public ResponseEntity<CommonBaseResult> updateGender(@RequestBody @Validated UserGenderUpdateDto userGenderUpdateDto,
        HttpServletRequest request)
        throws IOException {
        userService.editGender(userGenderUpdateDto);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(),"gender.modify.success",request.getLocale());
    }

    /**
     * 동네 설정 API
      *
     *
     * @return
     * @throws IOException
     */
    @PostMapping("/addresses")
    public ResponseEntity<CommonBaseResult> updateAddresses(@RequestBody @Validated UserAddressUpdateDto userAddressUpdateDto,
        HttpServletRequest request)
        throws IOException {
        userService.editAddress(userAddressUpdateDto);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(),"address.modify.success",request.getLocale());
    }


    /**
     * 동네 설정 API
     *
     *
     * @return
     * @throws IOException
     */
    @PutMapping("/age")
    public ResponseEntity<CommonBaseResult> updateAge(@RequestBody @Validated UserAgeUpdateDto userAgeUpdateDto,
        HttpServletRequest request)
        throws IOException {
        userService.editAge(userAgeUpdateDto);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(),"age.modify.success",request.getLocale());
    }


    /**
     * 소셜 계정 첫 password 업데이트 API
     * @param socialUserPwUpdateDto 소셜 계정 비밀번호
     * @return
     */
    @PutMapping("/password/social")
    public ResponseEntity<CommonBaseResult> updateSocialPassword(@RequestBody @Validated SocialUserPwUpdateDto socialUserPwUpdateDto,
                                                           HttpServletRequest request) throws IOException {
        userService.editSocialPassword(socialUserPwUpdateDto);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(),"pw.modify.success",request.getLocale());
    }





}
