package com.mungwithme.marking.service.marking;


import com.mungwithme.marking.model.dto.request.MarkingAddDto;
import com.mungwithme.marking.model.dto.request.MarkingModifyDto;
import com.mungwithme.marking.model.dto.request.MarkingRemoveDto;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MarkingTempService {

    private final MarkingService markingService;
    private final MarkingQueryService markingQueryService;

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
        if (images == null) {
            images = new ArrayList<>();
        }
        markingService.addMarking(markingAddDto, images, true);
    }

    /**
     * 유저가 작성한 임시 저장 마킹 수정
     *
     * @param markingModifyDto
     * @param images
     */
    @Transactional
    public void editTempMarking(MarkingModifyDto markingModifyDto, List<MultipartFile> images) {
        markingService.editMarking(markingModifyDto, images, true);
    }

    /**
     * 유저가 작성한 임시 저장 마킹 삭제
     *
     * @param markingRemoveDto
     */
    @Transactional
    public void removeTempMarking(MarkingRemoveDto markingRemoveDto) {
        markingService.removeMarking(markingRemoveDto, true);
    }

    public MarkingInfoResponseDto findTempMarkingInfoResponseDto(Long id, boolean isDeleted, boolean isTempSaved) {
        return markingService.findMarkingInfoResponseDto(
            id, isDeleted,
            isTempSaved);
    }

    /**
     * 유저의 임시 저장 마킹 수 조회
     * @param userId 유저PK
     * @return 임시 저장 마킹 수
     */
    public int countTempMarkingByUserId(Long userId) {
        return markingQueryService.countMarkingByUserId(userId);
    }
}
