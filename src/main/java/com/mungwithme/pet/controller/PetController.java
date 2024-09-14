package com.mungwithme.pet.controller;

import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.pet.model.dto.PetSignUpDto;
import com.mungwithme.pet.service.PetService;
import com.mungwithme.user.model.dto.UserResponseDto;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;
    private final BaseResponse baseResponse;
    private final UserService userService;

    /**
     * 회원가입 3단계 : [일반/소셜] 애완동물 정보 저장
     *
     * @param petSignUpDto 애완동물정보
     * @return
     */
    @PostMapping("")
    public ResponseEntity<CommonBaseResult> signUp3(@ModelAttribute PetSignUpDto petSignUpDto, HttpServletResponse response) throws Exception {

        UserResponseDto userResponseDto = new UserResponseDto();

        try {
            petSignUpDto.setUserId(userService.getCurrentUser().getId());  // UserDetails에서 유저 정보 조회
            User user = petService.signUp3(petSignUpDto);                  // 강쥐 정보 저장

            userResponseDto.setRole(user.getRole().getKey());

            return baseResponse.sendContentResponse(userResponseDto, 200);
        } catch (ResourceNotFoundException e) {
            return baseResponse.sendErrorResponse(404, "회원을 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error(e.getMessage());
            return baseResponse.sendErrorResponse(500, "예상치 못한 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.");
        }
    }

}
