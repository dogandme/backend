package com.mungwithme.pet.service;

import com.mungwithme.common.file.FileStore;
import com.mungwithme.pet.model.dto.PetSignUpDto;
import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.pet.repository.PetRepository;
import com.mungwithme.user.model.Role;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.repository.UserRepository;
import com.mungwithme.user.service.UserQueryService;
import com.mungwithme.user.service.UserService;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PetService {

    private final PetRepository petRepository;
    private  final UserQueryService userQueryService;
    private final FileStore fileStore;

    @Value("${com.example.ex8_fileupload.upload.path}") // application 의 properties 의 변수
    private String uploadPath;

    /**
     * 애완동물 정보 저장 및 USER권한 토큰 발행
     * @param petSignUpDto 애완동물정보
     */
    @Transactional
    public User addPet(PetSignUpDto petSignUpDto, List<MultipartFile> images) throws IOException {
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
        if (!user.getRole().equals(Role.USER)) {
            user.setRole(Role.USER);
        }

        return user;
    }

    /**
     * 강쥐 프로필 이미지 업로드
     * @param profile 업로드 할 파일
     */
    private String editFile(MultipartFile profile) throws IOException {

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
