package com.mungwithme.marking.service.marking;

import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.maps.dto.response.LocationBoundsDTO;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.repository.marking.MarkingQueryRepository;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserService;
import java.util.List;
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
//    private final LikesService likesService;
    private final UserService userService;

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

    /**
     *
     * 비공개
     * 위 경도 계산
     *
     * likedCount
     * savedCount
     *
     * 공개 범위 처리
     *
     * 위도와 경도
     *
     *
     *
     */

    /**
     * 주변 마킹 검색
     * @return
     */
    public List<MarkingInfoResponseDto> findNearbyMarkers(LocationBoundsDTO locationBoundsDTO) {
        boolean isMember = true;
        User currentUser = null;
        try {
            currentUser = userService.getCurrentUser();
        } catch (ResourceNotFoundException e) {
            isMember = false;
        }


        if (isMember) {
            markingQueryRepository.findNearbyMarkers(locationBoundsDTO.getSouthBottomLat(),
                locationBoundsDTO.getNorthTopLat(), locationBoundsDTO.getSouthLeftLng(),
                locationBoundsDTO.getNorthRightLng(), false, false, currentUser);
        } else {
            markingQueryRepository.findNearbyMarkersOnlyPublic(locationBoundsDTO.getSouthBottomLat(),
                locationBoundsDTO.getNorthTopLat(), locationBoundsDTO.getSouthLeftLng(),
                locationBoundsDTO.getNorthRightLng(), false, false);
        }


        return null;
    }


    /**
     * 좌표값을 이용한 바운더리 계산
     *
     *
     * @return
     */
    public List<Marking> findMarkingInBounds(
        double southBottomLat,
        double northTopLat,
        double southLeftLng,
        double northRightLng, boolean isDeleted, boolean isTempSaved) {
        return markingQueryRepository.findMarkingInBounds(
            southBottomLat, northTopLat, southLeftLng, northRightLng,
            isDeleted, isTempSaved);
    }




}
