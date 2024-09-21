package com.mungwithme.marking.service.marking;

import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.maps.dto.response.LocationBoundsDTO;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.model.dto.sql.MarkingQueryDto;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.repository.marking.MarkingQueryRepository;
import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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


    /**
     * 주변 마킹 검색  (회원 전용)
     *
     * @param southBottomLat
     * @param northTopLat
     * @param southLeftLng
     * @param northRightLng
     * @param isDeleted
     * @param isTempSaved
     * @param user
     * @return
     */
    public Set<MarkingQueryDto> findNearbyMarkers(
        double southBottomLat,
        double northTopLat,
        double southLeftLng,
        double northRightLng,
        boolean isDeleted,
        boolean isTempSaved,
        User user
    ) {
        return markingQueryRepository.findNearbyMarkers(southBottomLat, northTopLat, southLeftLng, northRightLng,
            isDeleted, isTempSaved, user);
    }

    /**
     * 주변마킹 검색 (비회원)
     *
     * @param southBottomLat
     *     남서쪽 위도
     * @param northTopLat
     *     북동쪽 위도
     * @param southLeftLng
     *     남서쪽 경도
     * @param northRightLng
     *     북동쪽 경도
     * @param isDeleted
     * @param isTempSaved
     * @return
     */
    public Set<MarkingQueryDto> findNearbyMarkersOnlyPublic(
        double southBottomLat,
        double northTopLat,
        double southLeftLng,
        double northRightLng,
        boolean isDeleted,
        boolean isTempSaved
    ) {
        return markingQueryRepository.findNearbyMarkersOnlyPublic(southBottomLat, northTopLat, southLeftLng,
            northRightLng,
            isDeleted, isTempSaved);
    }


    /**
     * 내 마킹 정보
     *
     * @param isDeleted
     * @param user
     * @return
     */
    public Set<MarkingQueryDto> findAllMarkersByUser(
        User user,
        boolean isDeleted,
        boolean isTempSaved

    ) {
        return markingQueryRepository.findAllMarkersByUser(isDeleted, isTempSaved, user.getId());
    }

    /**
     * 내 마킹 정보
     *
     * @param isDeleted
     * @param user
     * @return
     */
    public Set<MarkingQueryDto> findAllLikedMarkersByUser(
        User user,
        boolean isDeleted,
        boolean isTempSaved

    ) {
        return markingQueryRepository.findAllLikedMarkersByUser(isDeleted, isTempSaved, user.getId());
    }



    /**
     * 좌표값을 이용한 바운더리 계산
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
