package com.mungwithme.pet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.file.FileStore;
import com.mungwithme.pet.model.dto.request.PetRequestDto;
import com.mungwithme.pet.model.dto.response.PetInfoResponseDto;
import com.mungwithme.pet.model.dto.response.PetSignUpDto;
import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.pet.repository.PetRepository;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.model.enums.Role;
import com.mungwithme.user.model.dto.UserResponseDto;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserQueryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PetService {

    private final PetRepository petRepository;
    private  final UserQueryService userQueryService;
    private final FileStore fileStore;
    private final JwtService jwtService;
    private final PetQueryService petQueryService;
    private final ObjectMapper objectMapper;

    @Value("${com.example.ex8_fileupload.upload.path}") // application 의 properties 의 변수
    private String uploadPath;

    /**
     * 애완동물 정보 저장 및 USER권한 토큰 발행
     * @param petSignUpDto 애완동물정보
     */
    @Transactional
    public UserResponseDto addPet(PetSignUpDto petSignUpDto, List<MultipartFile> images, HttpServletRequest request) throws IOException {
        // UserDetails에서 user 엔터티 조회
        User user = userQueryService.findCurrentUser();
        // 강쥐 프로필 이미지 업로드
        List<String> profile = fileStore.uploadFiles(images, FileStore.PET_DIR);

        // 강쥐 DB 저장
        Pet pet = Pet.builder()
                .name(petSignUpDto.getName())
                .description(petSignUpDto.getDescription())
                .personalities(petSignUpDto.getPersonalities())
                .profile(profile.get(0))
                .breed(petSignUpDto.getBreed())
                .user(user)
                .build();
        petRepository.save(pet);

        String redisAuthToken = jwtService.getRedisAuthToken(request);

        // 기존에 USER 권한이 아니었을 경우 USER 권한으로 변경
        UserResponseDto userResponseDto = new UserResponseDto();
        if (!user.getRole().equals(Role.USER)) {
            user.setRole(Role.USER);
            String accessToken = jwtService.createAccessToken(user.getEmail(), user.getRole().getKey(),redisAuthToken);
            userResponseDto.setAuthorization(accessToken);
        }
        userResponseDto.setNickname(user.getNickname());
        userResponseDto.setRole(user.getRole().getKey());
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
            fileStore.deleteFile(FileStore.PET_DIR,profile);

        }
    }

    /**
     * 유저 닉네임으로 펫 조회
     * @param nickname 유저 닉네임
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
     * @param petDtoJson 수정 정보
     * @param image 수정 프로필 이미지
     */
    @Transactional
    public void editPet(String petDtoJson, List<MultipartFile> image) throws IOException {

        // UserDetails에서 user 엔터티 조회
        User user = userQueryService.findCurrentUser_v2();

        // 이전 강쥐 프로필 이미지 삭제
        Pet prevPet = petQueryService.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("error.notfound.pet"));
        fileStore.deleteFile(FileStore.PET_DIR, prevPet.getProfile());

        // JSON 문자열을 DTO로 변환
        PetRequestDto petRequestDto = objectMapper.readValue(petDtoJson, PetRequestDto.class);

        // 강쥐 프로필 이미지 업로드
        List<String> profile = fileStore.uploadFiles(image, FileStore.PET_DIR);

        // 강쥐 DB 저장
        petRepository.save(Pet.builder()
                .id(prevPet.getId())
                .name(petRequestDto.getName())
                .description(petRequestDto.getDescription())
                .personalities(petRequestDto.getPersonalities())
                .profile(profile.get(0))
                .breed(petRequestDto.getBreed())
                .user(user)
                .build());
    }
}
