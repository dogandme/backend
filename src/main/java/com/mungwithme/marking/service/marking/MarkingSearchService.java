package com.mungwithme.marking.service.marking;

import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.util.GeoUtils;
import com.mungwithme.likes.model.dto.response.LikeCountResponseDto;
import com.mungwithme.likes.model.enums.ContentType;
import com.mungwithme.likes.service.LikesService;
import com.mungwithme.maps.dto.response.LocationBoundsDTO;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.model.dto.sql.MarkingQueryDto;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MarkingSearchService {

    private final UserService userService;
    private final MarkingQueryService markingQueryService;
    private final LikesService likesService;

    /**
     * 주변 마킹 검색 API
     * 비회원 식별 후 검색 기능을 다르게
     * 보기 권한에 따른 쿼리
     *
     *
     *
     * @param locationBoundsDTO
     * @return
     */
    public List<MarkingInfoResponseDto> findNearbyMarkers(LocationBoundsDTO locationBoundsDTO) {
        boolean isMember = true;
        User currentUser = null;

        GeoUtils.isWithinKorea(locationBoundsDTO.getNorthTopLat(),
            locationBoundsDTO.getNorthRightLng());
        GeoUtils.isWithinKorea(locationBoundsDTO.getSouthBottomLat(),
            locationBoundsDTO.getSouthLeftLng());


        Set<MarkingQueryDto> nearbyMarkers = new HashSet<>();
        try {
            currentUser = userService.getCurrentUser();
        } catch (Exception e) {
            isMember = false;
        }




        if (isMember) {
            nearbyMarkers.addAll(markingQueryService.findNearbyMarkers(
                locationBoundsDTO.getSouthBottomLat(),
                locationBoundsDTO.getNorthTopLat(), locationBoundsDTO.getSouthLeftLng(),
                locationBoundsDTO.getNorthRightLng(), false, false, currentUser));
        } else {
            nearbyMarkers.addAll(
                markingQueryService.findNearbyMarkersOnlyPublic(locationBoundsDTO.getSouthBottomLat(),
                    locationBoundsDTO.getNorthTopLat(), locationBoundsDTO.getSouthLeftLng(),
                    locationBoundsDTO.getNorthRightLng(), false, false));
        }

        if (nearbyMarkers.isEmpty()) {
            throw new IllegalArgumentException("ex) 마킹 정보가 없습니다");
        }
        Map<Long, MarkingQueryDto> markingMap = nearbyMarkers.stream()
            .collect(Collectors.toMap(key -> key.getMarking().getId(), value -> value));

        Map<Long, LikeCountResponseDto> likeMap = likesService.fetchLikeCounts(markingMap.keySet(), ContentType.MARKING).stream()
            .collect(Collectors.toMap(LikeCountResponseDto::getContentId, value -> value));

        List<MarkingInfoResponseDto> markingInfoList = new ArrayList<>(markingMap.size());
        for (Map.Entry<Long, MarkingQueryDto> entry : markingMap.entrySet()) {
            Long id = entry.getKey();
            MarkingQueryDto nearbyMarker = entry.getValue();
            Marking marking = nearbyMarker.getMarking();
            Pet pet = nearbyMarker.getPet();

            log.info("여기까지 -------");

            // 한번에 모든 데이터를 설정하여 객체 초기화를 효율적으로 수행
            MarkingInfoResponseDto markingInfoResponseDto = new MarkingInfoResponseDto(marking);

            // 작성자인지 확인
            if (isMember) {
                markingInfoResponseDto.updateIsOwner(currentUser.getEmail().equals(marking.getUser().getEmail()));
            }

            // Pet 정보 업데이트
            markingInfoResponseDto.updatePet(pet);

            // 좋아요 수가 있는 경우 업데이트
            LikeCountResponseDto likeCountResponseDto = likeMap.get(id);
            if (likeCountResponseDto != null) {
                markingInfoResponseDto.updateLikeCount(likeCountResponseDto.getCount());
            }

            markingInfoList.add(markingInfoResponseDto);
        }

        log.info("markingInfoList.size() = {}", markingInfoList.size());
        return markingInfoList;
    }

}
