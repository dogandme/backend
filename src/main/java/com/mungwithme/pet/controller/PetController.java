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
import org.springframework.http.HttpStatus;
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
     * @param petSignUpDtoJson
     *     애완동물정보
     * @return
     */
    @PostMapping("")
    public ResponseEntity<CommonBaseResult> signUp3(
        @RequestPart(name = "petSignUpDto") String petSignUpDtoJson,
        @RequestPart(name = "image") List<MultipartFile> image) throws Exception {

        UserResponseDto userResponseDto = new UserResponseDto();

        PetSignUpDto petSignUpDto = objectMapper.readValue(petSignUpDtoJson, PetSignUpDto.class);   // JSON 문자열을 DTO로 변환
        User user = petService.signUp3(petSignUpDto, image);                                        // 강쥐 정보 저장

        userResponseDto.setRole(user.getRole().getKey());

        return baseResponse.sendContentResponse(userResponseDto, HttpStatus.OK.value());

    }

}
