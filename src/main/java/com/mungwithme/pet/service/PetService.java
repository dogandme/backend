package com.mungwithme.pet.service;

import com.mungwithme.common.file.FileStore;
import com.mungwithme.pet.model.dto.PetSignUpDto;
import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.pet.repository.PetRepository;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.model.Role;
import com.mungwithme.user.model.dto.UserResponseDto;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.repository.UserRepository;
import com.mungwithme.user.service.UserQueryService;
import com.mungwithme.user.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PetService {

    private final PetRepository petRepository;
    private  final UserQueryService userQueryService;
    private final FileStore fileStore;
    private final JwtService jwtService;

    @Value("${com.example.ex8_fileupload.upload.path}") // application 의 properties 의 변수
    private String uploadPath;

    /**
     * 애완동물 정보 저장 및 USER권한 토큰 발행
     * @param petSignUpDto 애완동물정보
     */
    @Transactional
    public UserResponseDto addPet(PetSignUpDto petSignUpDto, List<MultipartFile> images) throws IOException {
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

        // 기존에 USER 권한이 아니었을 경우 USER 권한으로 변경
        UserResponseDto userResponseDto = new UserResponseDto();
        if (!user.getRole().equals(Role.USER)) {
            user.setRole(Role.USER);
            String accessToken = jwtService.createAccessToken(user.getEmail(), user.getRole().getKey());
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
        Pet pet = findByUser(user).orElse(null);

        if (pet != null) {

            String profile = pet.getProfile();
//            petRepository.delete(pet);
            // Pet 이미지 삭제
            fileStore.deleteFile(FileStore.PET_DIR,profile);

        }
    }

    public Optional<Pet> findByUser(User user) {
        return petRepository.findByUser(user.getId());
    }
}
