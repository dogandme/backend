package com.mungwithme.marking.service;


import com.mungwithme.common.file.FileStore;
import com.mungwithme.marking.model.dto.request.MarkingAddDto;
import com.mungwithme.marking.model.dto.request.MarkingModifyDto;
import com.mungwithme.marking.model.dto.request.MarkingRemoveDto;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.model.entity.MarkImage;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.repository.markImge.MarkImageRepository;
import com.mungwithme.marking.repository.impl.MarkImageRepositoryImpl;
import com.mungwithme.marking.repository.marking.MarkingRepository;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MarkingService {


    private final UserService userService;

    private final MarkingQueryService markingQueryService;
    private final FileStore fileStore;
    private final MarkImageRepository markImageRepository;
    private final MarkingRepository markingRepository;
    private final MarkImageRepositoryImpl markImageRepositoryImpl;

    public final int MAX_IMAGE_UPLOAD_SIZE = 5;


    /**
     * 유저가 작성한 마킹 임시 저장
     *
     * @param markingAddDto
     *     마킹 정보
     * @param images
     *     마킹 이미지 최소 1개
     */
    @Transactional
    public void addTempMarking(MarkingAddDto markingAddDto, List<MultipartFile> images) {
        addMarking(markingAddDto, images, true);
    }

    /**
     * 유저가 작성한 임시 저장 마킹 수정
     *
     * @param markingModifyDto
     * @param images
     */
    @Transactional
    public void patchTempMarking(MarkingModifyDto markingModifyDto, List<MultipartFile> images) {
        patchMarking(markingModifyDto, images, true);
    }

    /**
     * 유저가 작성한 임시 저장 마킹 삭제
     *
     * @param markingRemoveDto
     */
    @Transactional
    public void deleteTempMarking(MarkingRemoveDto markingRemoveDto) {
        deleteMarking(markingRemoveDto, true);
    }


    /**
     * @param markingAddDto
     *     마킹 정보
     * @param images
     *     마킹 이미지 최소 1개
     * @param isTempSaved
     *     임시 저장 여부
     */
    @Transactional
    public void addMarking(MarkingAddDto markingAddDto, List<MultipartFile> images, boolean isTempSaved) {
        imageSizeCheck(images);
        User user = getUser();

        Marking marking = Marking.create(markingAddDto, user);

        // 임시저장 여부
        marking.updateIsTempSaved(isTempSaved);

        markingRepository.save(marking);

        try {
            // 파일 업로드
            List<String> fileNames = fileStore.uploadFiles(images, FileStore.MARKING_DIR);

            // 파일 이름과 인덱스를 사용해 MarkImage 객체 리스트 생성
            List<MarkImage> markImages = IntStream.range(0, fileNames.size())
                .mapToObj(i -> MarkImage.create(marking, fileNames.get(i), i))
                .collect(Collectors.toList());

            // MarkImage 리스트와 현재 시간을 데이터베이스에 저장
            markImageRepositoryImpl.saveAll(markImages, LocalDateTime.now());
        } catch (IOException e) {
            // 예외 발생 시 로그 메시지를 error 수준으로 출력
            log.error("File upload failed: {}", e.getMessage(), e);
        }

    }

    /**
     *
     */
    @Transactional
    public void deleteMarking(MarkingRemoveDto markingRemoveDto, boolean isTempSaved) {
        User user = getUser();

        // 마킹 query
        Marking marking = markingQueryService.findById(markingRemoveDto.getId(), false, isTempSaved);

        assert user != null;

        // Email 비교 값으로 권한 확인
        if (!user.getEmail().equals(marking.getUser().getEmail())) {
            throw new IllegalArgumentException("ex) 삭제 권한이 없습니다.");
        }

        Set<MarkImage> images = marking.getImages();

        for (MarkImage image : images) {
            fileStore.deleteFile(FileStore.MARKING_DIR, image.getImageUrl());
        }

        // isDeleted true 로 업데이트
        marking.updateIsDeleted(true);

        // 삭제
        markImageRepository.deleteAllInBatch(images);
    }


    @Transactional
    public void patchMarking(MarkingModifyDto markingModifyDto, List<MultipartFile> images, boolean isTempSaved) {
//        imageSizeCheck(images);

        // 마킹 query
        Marking marking = markingQueryService.findById(markingModifyDto.getId(), false, isTempSaved);

        User user = getUser();

        // Email 비교 값으로 권한 확인
        if (!user.getEmail().equals(marking.getUser().getEmail())) {
            throw new IllegalArgumentException("ex) 수정 권한이 없습니다.");
        }

        // 업데이트
        marking.updateContent(markingModifyDto.getContent());

        marking.updateIsVisible(markingModifyDto.getIsVisible());

        // 이미지 삭제 ids
        Set<Long> removeIds = markingModifyDto.getRemoveIds();

        Map<Long, MarkImage> imageMap = marking.getImages().stream()
            .collect(Collectors.toMap(MarkImage::getId, value -> value));

        // 삭제할 이미지가 있으면 삭제
        // 사용자가 보낸 이미지 Id 가 DB 에 존재하는지 확인
        List<MarkImage> removeMarkImage = removeIds.stream()
            .map(imageMap::remove)         // removeId에 해당하는 MarkImage를 제거하고 반환
            .filter(Objects::nonNull).toList(); // 리스트로 수집

        // 기존 이미지 갯수
        int existSize = marking.getImages().size();

        // 삭제 이미지 갯수
        int removeSize = removeMarkImage.size();

        // 추가 이미지 갯수
        int addSize = images.size();

        int allSize = (existSize - removeSize) + addSize;

        if (allSize <= 0 || MAX_IMAGE_UPLOAD_SIZE < allSize) {
            throw new IllegalArgumentException("ex) 이미지 파일을 다시 확인 해주세요");
        }

        // markImage db 삭제
        if (!removeMarkImage.isEmpty()) {
            removeMarkImage.forEach(remove -> fileStore.deleteFile(FileStore.MARKING_DIR, remove.getImageUrl()));
            markImageRepository.deleteAllInBatch(removeMarkImage);
        }

        try {
            // 파일 업로드
            List<String> fileNames = fileStore.uploadFiles(images, FileStore.MARKING_DIR);
            // 가장 큰 order
            int maxLnk = 0;
            if (!images.isEmpty()) {
                maxLnk = imageMap.values().stream()
                    .max(Comparator.comparingLong(MarkImage::getLank))
                    .map(markImage -> markImage.getLank() + 1)
                    .orElse(0);
            }
            // 파일 이름과 인덱스를 사용해 MarkImage 객체 리스트 생성
            final int finalMaxLnk = maxLnk;
            List<MarkImage> markImages = IntStream.range(0, fileNames.size())
                .mapToObj(i -> MarkImage.create(marking, fileNames.get(i), finalMaxLnk + i))
                .collect(Collectors.toList());

            // MarkImage 리스트와 현재 시간을 데이터베이스에 저장
            markImageRepositoryImpl.saveAll(markImages, LocalDateTime.now());
        } catch (IOException e) {
            // 예외 발생 시 로그 메시지를 error 수준으로 출력
            log.error("File upload failed: {}", e.getMessage(), e);
        }
    }


    public MarkingInfoResponseDto getMarkingInfoResponseDto(Long id, boolean isDeleted, boolean isTempSaved) {
        User user = getUser();

        MarkingInfoResponseDto markingInfoResponseDto = markingQueryService.fetchMarkingInfoDto(user, id, isDeleted,
            isTempSaved);

        if (isTempSaved && !markingInfoResponseDto.getIsOwner()) {
            throw new IllegalArgumentException("ex) 접근 권한이 없습니다.");
        }
        return markingInfoResponseDto;
    }


    private User getUser() {
        //
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return userService.findByEmail(userDetails.getUsername()).orElse(null);

//        return userService.findByEmail("lim642666@gmail.com").orElse(null);
    }


    private void imageSizeCheck(List<MultipartFile> images) {
        if (images.size() > MAX_IMAGE_UPLOAD_SIZE || images.isEmpty()) {
            throw new IllegalArgumentException("ex) 이미지 파일을 다시 확인 해주세요");
        }
    }
}
