package com.mungwithme.marking.service.marking;

import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.repository.marking.MarkingQueryRepository;
import com.mungwithme.marking.repository.marking.MarkingRepository;
import com.mungwithme.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MarkingQueryService {


    private final MarkingQueryRepository markingQueryRepository;

    /**
     * 마킹 상세 정보 검색 API
     *
     * @param id
     *     마킹 고유값
     * @param isDeleted
     *     마킹 삭제 여부
     * @param isTempSaved
     *     마킹 임시 저장 여부
     * @return
     */
    public Marking findById(long id, boolean isDeleted, boolean isTempSaved) {
        return markingQueryRepository.findById(id, isDeleted, isTempSaved)
            .orElseThrow(() -> new IllegalArgumentException("ex) 찾고자 하는 마킹 정보가 없습니다."));
    }

    /**
     * marking
     *
     */

    /**
     * marking 상세 정보 검색 API
     *
     * @param id
     *     marking 아이디
     * @param isDeleted
     *     삭제여부
     * @param isTempSaved
     *     임시저장 여부
     * @return
     */
    public MarkingInfoResponseDto fetchMarkingInfoDto(User user, long id, boolean isDeleted, boolean isTempSaved) {

        Marking findMarking = findById(id, isDeleted, isTempSaved);

        MarkingInfoResponseDto markingInfoResponseDto = new MarkingInfoResponseDto(findMarking);

        markingInfoResponseDto.updateIsOwner(user.getEmail().equals(findMarking.getUser().getEmail()));

        return markingInfoResponseDto;
    }

}
