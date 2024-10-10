package com.mungwithme.pet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.file.FileStore;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.pet.model.dto.request.PetRequestDto;
import com.mungwithme.pet.model.dto.response.PetSignUpDto;
import com.mungwithme.pet.service.PetQueryService;
import com.mungwithme.pet.service.PetService;
import com.mungwithme.user.model.dto.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
    private final FileStore fileStore;

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
        @RequestPart(name = "image", required = false) MultipartFile image, HttpServletRequest request) throws Exception {

        PetSignUpDto petSignUpDto = objectMapper.readValue(petSignUpDtoJson, PetSignUpDto.class);   // JSON 문자열을 DTO로 변환

        UserResponseDto userResponseDto = petService.addPet(petSignUpDto, image, request);

        return baseResponse.sendContentResponse(userResponseDto, HttpStatus.OK.value());

    }

    /**
     * 펫 정보 조회
     *
     * @param nickname
     *     유저 닉네임
     * @return 펫 정보
     */
    @GetMapping("")
    public ResponseEntity<CommonBaseResult> getPet(@RequestParam("nickname") String nickname) throws IOException {
        return baseResponse.sendContentResponse(petService.findPetByNickname(nickname), HttpStatus.OK.value());
    }

    /**
     * 펫 정보 수정
     *
     * @param petDtoJson
     *     펫 수정 정보
     * @param image
     *     펫 수정 프로필 이미지
     * @return
     */
    @PutMapping("")
    public ResponseEntity<CommonBaseResult> updatePet(
        @RequestPart(name = "petDto") String petDtoJson,
        @RequestPart(name = "image",required = false) MultipartFile image,
        HttpServletRequest request) throws IOException {
        petService.editPet(petDtoJson, image);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(), "pet.modify.success", request.getLocale());
    }


    /**
     * id:user_file_Api_1
     * <p>
     * 서버에서 펫 이미지 가져오기
     *
     * @return
     * @throws IOException
     */
    @GetMapping("/image/{fileName}")
    public ResponseEntity<UrlResource> getPetImage(@PathVariable(name = "fileName") String fileName) {
        // file MediaType 확인 후 header 에 저장
        MediaType mediaType = null;
        UrlResource pictureImage = null;
        try {
            if (StringUtils.hasText(fileName)) {
                mediaType = MediaType.parseMediaType(Files.probeContentType(Paths.get(fileName)));
                pictureImage = fileStore.getUrlResource(fileName, FileStore.PET_DIR);
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("error.notfound.image");
        }
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
            .body(pictureImage);
    }

}
