package com.mungwithme.pet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.pet.model.dto.request.PetRequestDto;
import com.mungwithme.pet.model.dto.response.PetSignUpDto;
import com.mungwithme.pet.service.PetQueryService;
import com.mungwithme.pet.service.PetService;
import com.mungwithme.user.model.dto.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ResponseEntity<CommonBaseResult> createPet(
        @RequestPart(name = "petSignUpDto") String petSignUpDtoJson,
        @RequestPart(name = "image") List<MultipartFile> image) throws Exception {

        PetSignUpDto petSignUpDto = objectMapper.readValue(petSignUpDtoJson, PetSignUpDto.class);   // JSON 문자열을 DTO로 변환

        UserResponseDto userResponseDto = petService.addPet(petSignUpDto, image);

        return baseResponse.sendContentResponse(userResponseDto, HttpStatus.OK.value());

    }

    /**
     * 펫 정보 조회
     * @param nickname 유저 닉네임
     * @return 펫 정보
     */
    @GetMapping("")
    public ResponseEntity<CommonBaseResult> getPet(@RequestParam("nickname") String nickname) throws IOException {
        return baseResponse.sendContentResponse(petService.findPetByNickname(nickname), HttpStatus.OK.value());
    }

    /**
     * 펫 정보 수정
     * @param petDtoJson 펫 수정 정보
     * @param image 펫 수정 프로필 이미지
     * @return
     */
    @PutMapping("")
    public ResponseEntity<CommonBaseResult> updatePet(
            @RequestPart(name = "petDto") String petDtoJson,
            @RequestPart(name = "image") List<MultipartFile> image,
            HttpServletRequest request) throws IOException {
        petService.editPet(petDtoJson, image);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(), "pet.modify.success", request.getLocale());
    }

}
