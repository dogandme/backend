package com.mungwithme.marking.service.marking;

import static com.mungwithme.marking.model.entity.QMarking.marking;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.model.dto.sql.MarkingQueryDto;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.model.entity.QMarking;
import com.mungwithme.marking.model.enums.SortType;
import com.mungwithme.marking.repository.marking.MarkingQueryRepository;
import com.mungwithme.user.model.entity.User;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.query.JpaQueryCreator;
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
            .orElseThrow(() -> new ResourceNotFoundException("error.notfound.markings"));
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
    public MarkingInfoResponseDto findMarkingInfoDto(User user, long id, boolean isDeleted, boolean isTempSaved) {

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
     * 동네 마킹 검색  (회원 전용)
     *
     * @param lat
     *     현재 위치
     * @param lng
     *     현재 위치
     * @param sortType
     *     정렬 기준
     * @param isDeleted
     * @param isTempSaved
     * @param user
     * @return
     */
    public Page<MarkingQueryDto> findNearbyMarkers(
        User user,
        Set<Address> addresses,
        double lat,
        double lng,
        SortType sortType,
        boolean isDeleted,
        boolean isTempSaved,
        int offset,
        int pageSize
    ) {
        PageRequest pageRequest = PageRequest.of(offset, pageSize);
        if (sortType.equals(SortType.RECENT)) {
            return markingQueryRepository.findMarkersOrderByRegDtDesc(lat, lng, addresses, isDeleted, isTempSaved, user,
                pageRequest);
        } else if (sortType.equals(SortType.DISTANCE)) {
            return markingQueryRepository.findMarkersOrderByDistAsc(lat, lng, addresses, isDeleted, isTempSaved, user,
                pageRequest);
        }
        return markingQueryRepository.findMarkersOrderByLikesDesc(lat, lng, addresses, isDeleted, isTempSaved, user,
            pageRequest);

    }


    /**
     * 동네 마킹 검색  (비 회원 전용)
     *
     * @param lat
     *     현재 위치
     * @param lng
     *     현재 위치
     * @param sortType
     *     정렬 기준
     * @param isDeleted
     * @param isTempSaved
     * @return
     */
    public Page<MarkingQueryDto> findNearbyMarkers(
        Set<Address> addresses,
        double lat,
        double lng,
        SortType sortType,
        boolean isDeleted,
        boolean isTempSaved,
        int offset,
        int pageSize
    ) {
        PageRequest pageRequest = PageRequest.of(offset, pageSize);
        if (sortType.equals(SortType.RECENT)) {
            return markingQueryRepository.findMarkersOrderByRegDtDesc(lat, lng, addresses, isDeleted, isTempSaved,
                pageRequest);
        } else if (sortType.equals(SortType.DISTANCE)) {
            return markingQueryRepository.findMarkersOrderByDistAsc(lat, lng, addresses, isDeleted, isTempSaved,
                pageRequest);
        }
        return markingQueryRepository.findMarkersOrderByLikesDesc(lat, lng, addresses, isDeleted, isTempSaved,
            pageRequest);

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
    public long findTempCount(

        User user,
        boolean isDeleted,
        boolean isTempSaved
    ) {

        return markingQueryRepository.findTempCount(isDeleted, isTempSaved, user.getId());
    }


    /**
     * 내 마킹 정보
     *
     * @param isDeleted
     * @param user
     * @return
     */
    public Page<MarkingQueryDto> findAllMarkersByUser(
        double lat,
        double lng,
        User user,
        boolean isDeleted,
        boolean isTempSaved,
        int offset,
        int pageSize,
        SortType sortType
    ) {

        PageRequest pageRequest = PageRequest.of(offset, pageSize);


        if (sortType.equals(SortType.RECENT)) {
            return markingQueryRepository.findAllMarkersByUserRegDtDesc(lat, lng, isDeleted, isTempSaved,
                user.getId(),pageRequest);
        } else if (sortType.equals(SortType.DISTANCE)) {
            return markingQueryRepository.findAllMarkersByUserDistAsc(lat, lng, isDeleted, isTempSaved,user.getId(),
                pageRequest);
        }
        return markingQueryRepository.findAllMarkersByUserLikesDesc(lat, lng, isDeleted, isTempSaved,user.getId(),
            pageRequest);

    }


    /**
     * 내가 좋아요한 마킹리스트
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
     * 내가 업로드한 마킹 리스트 (임시저장 포함)
     *
     * @param isDeleted
     * @param user
     * @return
     */
    public Set<Marking> findAll(
        User user,
        boolean isDeleted
    ) {
        return markingQueryRepository.findAll(user.getId(), isDeleted);
    }

    /**
     * 내가 즐겨찾기 한  마킹리스트
     *
     * @param isDeleted
     * @param user
     * @return
     */
    public Set<MarkingQueryDto> findAllSavedMarkersByUser(
        User user,
        boolean isDeleted,
        boolean isTempSaved
    ) {
        return markingQueryRepository.findAllSavedMarkersByUser(isDeleted, isTempSaved, user.getId());
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

    /**
     * 유저의 마킹 수 조회
     *
     * @param userId
     *     유저PK
     * @return 마킹 수
     */
    public int countMarkingByUserId(Long userId) {
        return markingQueryRepository.countByUserId(userId);
    }


    public Set<Marking> findMarkingsByUser (boolean isDeleted,boolean isTempSaved,long userId) {
        return markingQueryRepository.findMarkingsByUser(isDeleted, isTempSaved, userId);
    }


}
