package com.mungwithme.pet.service;

import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.pet.Base64DecodedMultipartFile;
import com.mungwithme.pet.model.dto.FileUploadDto;
import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.pet.repository.PetRepository;
import com.mungwithme.user.model.Role;
import com.mungwithme.pet.model.dto.PetSignUpDto;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Value("${com.example.ex8_fileupload.upload.path}") // application 의 properties 의 변수
    private String uploadPath;

    /**
     * 애완동물 정보 저장 및 USER권한 토큰 발행
     * @param petSignUpDto 애완동물정보
     */
    public User signUp3(PetSignUpDto petSignUpDto) throws IOException {

        // userId로 user entity 조회
        User user = userRepository.findById(petSignUpDto.getUserId())
                .map(updatedUser -> {
                    updatedUser.setRole(Role.USER);
                    return userRepository.save(updatedUser);
                })
                .orElseThrow(() -> new ResourceNotFoundException("회원 조회 실패"));

        // 강쥐 프로필 이미지 업로드
        String profile = uploadFile(petSignUpDto.getProfile());

        Pet pet = Pet.builder()
                .name(petSignUpDto.getName())
                .description(petSignUpDto.getDescription())
                .personalities(petSignUpDto.getPersonalities())
                .profile(profile)
                .breed(petSignUpDto.getBreed())
                .user(user)
                .build();

        petRepository.save(pet);    // DB 저장 Todo 프로필 저장 확인

        return user;
    }

    /**
     * 강쥐 프로필 이미지 업로드
     * @param profile 업로드 할 파일
     */
    private String uploadFile(MultipartFile profile) throws IOException {

        // 이미지 파일만 업로드
        if (!Objects.requireNonNull(profile.getContentType()).startsWith("image")) {
            // Todo 예외처리 필요
        }

        String folderPath = makeFolder();           // 날짜 폴더 생성
        String uuid = UUID.randomUUID().toString(); // UUID 생성

        String saveName = uploadPath + File.separator + folderPath + File.separator + uuid + "_" + profile.getName();
        Path savePath = Paths.get(saveName);

        // 실제 파일 저장
        profile.transferTo(savePath);

        return saveName;
    }

    /**
     * 날짜 폴더 생성
     * @return 폴더 경로
     */
    private String makeFolder() {
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String folderPath = str.replace("/", File.separator);

        File uploadPathFolder = new File(uploadPath, folderPath);

        if (!uploadPathFolder.exists()) {
            boolean mkdirs = uploadPathFolder.mkdirs();
        }

        return folderPath;
    }
}
