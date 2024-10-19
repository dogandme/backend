package com.mungwithme.pet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.file.FileStore;
import com.mungwithme.pet.model.dto.request.PetRequestDto;
import com.mungwithme.pet.model.dto.response.PetInfoResponseDto;
import com.mungwithme.pet.model.dto.response.PetSignUpDto;
import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.pet.model.entity.Pet.PetBuilder;
import com.mungwithme.pet.repository.PetRepository;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.model.enums.Role;
import com.mungwithme.user.model.dto.UserResponseDto;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserQueryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PetService {

    private final PetRepository petRepository;
    private final UserQueryService userQueryService;
    private final FileStore fileStore;
    private final JwtService jwtService;
    private final PetQueryService petQueryService;
    private final ObjectMapper objectMapper;

    @Value("${com.example.ex8_fileupload.upload.path}") // application 의 properties 의 변수
    private String uploadPath;

    /**
     * 애완동물 정보 저장 및 USER권한 토큰 발행
     *
     * @param petSignUpDto
     *     애완동물정보
     */
    @Transactional
    public UserResponseDto addPet(PetSignUpDto petSignUpDto, MultipartFile image, HttpServletRequest request,
        HttpServletResponse response)
        throws IOException {
        // UserDetails에서 user 엔터티 조회
        User user = userQueryService.findCurrentUser();

        String profile = null;

        if (image != null && !image.isEmpty()) {
            // 강쥐 프로필 이미지 업로드
            profile = fileStore.uploadFile(image, FileStore.PET_DIR);
        }

        // 강쥐 DB 저장
        Pet pet = Pet.builder()
            .name(petSignUpDto.getName())
            .description(petSignUpDto.getDescription())
            .personalities(petSignUpDto.getPersonalities())
            .breed(petSignUpDto.getBreed())
            .profile(profile)
            .user(user).build();
        // 이미지가 있는 경우 추가

        petRepository.save(pet);

        String redisAuthToken = jwtService.getRedisAuthToken(request);

        // 기존에 USER 권한이 아니었을 경우 USER 권한으로 변경
        UserResponseDto userResponseDto = new UserResponseDto();
        if (!user.getRole().equals(Role.USER)) {
            user.setRole(Role.USER);
            String accessToken = jwtService.createAccessToken(user.getEmail(), user.getRole().getKey(), redisAuthToken);
            userResponseDto.setAuthorization(accessToken);
        }

        userResponseDto.setNickname(user.getNickname());
        userResponseDto.setRole(user.getRole().getKey());

        String refreshToken = jwtService.createRefreshToken(user.getEmail(), Role.USER.getKey(),
            redisAuthToken)
            ;
        // RefreshToken 발급
        jwtService.setRefreshTokenCookie(response, refreshToken);                          // RefreshToken 쿠키에 저장

        return userResponseDto;
    }


    /**
     * pet 삭제
     *
     * @param user
     * @throws IOException
     */
    @Transactional
    public void deletePet(User user) {
        Pet pet = petQueryService.findByUser(user).orElse(null);

        if (pet != null) {
            String profile = pet.getProfile();
//            petRepository.delete(pet);
            // Pet 이미지 삭제
            fileStore.deleteFile(FileStore.PET_DIR, profile);
        }
    }

    /**
     * 유저 닉네임으로 펫 조회
     *
     * @param nickname
     *     유저 닉네임
     * @return 펫 정보
     */
    public PetInfoResponseDto findPetByNickname(String nickname) {
        // 닉네임 조회
        User user = userQueryService.findByNickname(nickname)
            .orElseThrow(() -> new ResourceNotFoundException("error.notfound.nickname"));

        // 펫 조회
        Pet pet = petQueryService.findByUser(user)
            .orElseThrow(() -> new ResourceNotFoundException("error.notfound.pet"));

        return PetInfoResponseDto.builder()
            .petId(pet.getId())
            .name(pet.getName())
            .description(pet.getDescription())
            .profile(pet.getProfile())
            .breed(pet.getBreed())
            .personalities(pet.getPersonalities())
            .build();
    }

    /**
     * 펫 정보 수정
     *
     * 사용자가 이미지만 삭제한 경우
     * 사용자가 이미지를 삭제하고 이미지를 추가한 경우
     * 이미지를 추가한 경우
     *
     *
     * @param petDtoJson
     *     수정 정보
     * @param image
     *     수정 프로필 이미지
     */
    @Transactional
    public void editPet(String petDtoJson, MultipartFile image) throws IOException {
        // JSON 문자열을 DTO로 변환
        PetRequestDto petRequestDto = objectMapper.readValue(petDtoJson, PetRequestDto.class);

        // UserDetails에서 user 엔터티 조회
        User user = userQueryService.findCurrentUser_v2();

        // 이전 강쥐 프로필 이미지 삭제
        Pet prevPet = petQueryService.findByUser(user)
            .orElseThrow(() -> new ResourceNotFoundException("error.notfound.pet"));

        // 펫의 원래 이미지가 있는데 사용자가 profile 이미지를 삭제 한 경우
        //  prevPet.getProfile != null && isProfile true 인경우
        if (StringUtils.hasText(prevPet.getProfile())  && petRequestDto.getIsChaProfile()) {
            fileStore.deleteFile(FileStore.PET_DIR, prevPet.getProfile());
        }

        // 강쥐 프로필 이미지 업로드
        String profile = null;
        if (image != null && !image.isEmpty()) {
            profile = fileStore.uploadFile(image, FileStore.PET_DIR);
        }

//        PetBuilder builder = Pet.builder()
//            .id(prevPet.getId())
//            .name(petRequestDto.getName())
//            .description(petRequestDto.getDescription())
//            .personalities(petRequestDto.getPersonalities())
//            .breed(petRequestDto.getBreed())
//            .user(user);
//
        prevPet.updateBreed(petRequestDto.getBreed());
        prevPet.updateName(petRequestDto.getName());
        prevPet.updatePersonalities(petRequestDto.getPersonalities());
        prevPet.updateDescription(petRequestDto.getDescription());

        // Profile 사진을 변경 했을 경우
        if (petRequestDto.getIsChaProfile()) {
            prevPet.updateProfile(profile);
        }

        // 강쥐 DB 저장
    }
}
