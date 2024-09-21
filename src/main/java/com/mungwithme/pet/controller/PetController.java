package com.mungwithme.pet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;
    private final BaseResponse baseResponse;
    private final ObjectMapper objectMapper;

    /**
     * 회원가입 3단계 : [일반/소셜] 애완동물 정보 저장
     *
     * @param petSignUpDtoJson 애완동물정보
     * @return
     */
    @PostMapping("")
    public ResponseEntity<CommonBaseResult> signUp3(
            @RequestPart(name = "petSignUpDto") String petSignUpDtoJson,
            @RequestPart(name = "image") List<MultipartFile> image) throws Exception {

        UserResponseDto userResponseDto = new UserResponseDto();

        try {
            PetSignUpDto petSignUpDto = objectMapper.readValue(petSignUpDtoJson, PetSignUpDto.class);   // JSON 문자열을 DTO로 변환
            User user = petService.signUp3(petSignUpDto, image);                                        // 강쥐 정보 저장

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
