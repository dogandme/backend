package com.mungwithme.marking.service.marking;

import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.util.GeoUtils;
import com.mungwithme.likes.model.dto.response.LikeCountResponseDto;
import com.mungwithme.likes.model.entity.Likes;
import com.mungwithme.likes.model.enums.ContentType;
import com.mungwithme.likes.service.LikesService;
import com.mungwithme.maps.dto.response.LocationBoundsDto;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.model.dto.response.MyMarkingsResponseDto;
import com.mungwithme.marking.model.dto.response.MyTempMarkingsResponseDto;
import com.mungwithme.marking.model.dto.sql.MarkingQueryDto;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.model.entity.MarkingSaves;
import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserQueryService;
import com.mungwithme.user.service.UserService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MarkingSearchService {

    private final UserQueryService userQueryService;
    private final MarkingQueryService markingQueryService;
    private final LikesService likesService;

    /**
     * 동네 마킹 검색 API
     * 보기 권한에 따른 쿼리 처리`
     *
     * @param locationBoundsDto
     * @return
     */

    public List<MarkingInfoResponseDto> findNearbyMarkers(LocationBoundsDto locationBoundsDto) {
        GeoUtils.isWithinKorea(locationBoundsDto.getNorthTopLat(),
            locationBoundsDto.getNorthRightLng());
        GeoUtils.isWithinKorea(locationBoundsDto.getSouthBottomLat(),
            locationBoundsDto.getSouthLeftLng());

        Set<MarkingQueryDto> nearbyMarkers = new HashSet<>();
        User currentUser = userQueryService.findCurrentUser_v2();

        boolean isMember = currentUser != null;

        if (isMember) {
            nearbyMarkers.addAll(markingQueryService.findNearbyMarkers(
                locationBoundsDto.getSouthBottomLat(),
                locationBoundsDto.getNorthTopLat(), locationBoundsDto.getSouthLeftLng(),
                locationBoundsDto.getNorthRightLng(), false, false, currentUser));
        } else {
            nearbyMarkers.addAll(
                markingQueryService.findNearbyMarkersOnlyPublic(locationBoundsDto.getSouthBottomLat(),
                    locationBoundsDto.getNorthTopLat(), locationBoundsDto.getSouthLeftLng(),
                    locationBoundsDto.getNorthRightLng(), false, false));
        }

        if (nearbyMarkers.isEmpty()) {
            throw new ResourceNotFoundException("error.notfound.markings");
        }
        return findMarkingInfoResponseDtoList(isMember, currentUser, nearbyMarkers);
    }


    /**
     * 나의 좋아요 마킹 리스트 출력 API
     *
     */
    public List<MarkingInfoResponseDto> findAllLikedMarkersByUser() {
        User myUser = userQueryService.findCurrentUser();

        Set<MarkingQueryDto> markingQueryDtoSet = new HashSet<>();

        markingQueryDtoSet.addAll(markingQueryService.findAllLikedMarkersByUser(myUser, false, false));

        return findMarkingInfoResponseDtoList(true, myUser,
            markingQueryDtoSet);
    }

    /**
     * 나의 즐겨찾기 마킹 리스트 출력 API
     *
     */
    public List<MarkingInfoResponseDto> findAllSavedMarkersByUser() {
        User myUser = userQueryService.findCurrentUser();

        Set<MarkingQueryDto> markingQueryDtoSet = new HashSet<>();

        markingQueryDtoSet.addAll(markingQueryService.findAllSavedMarkersByUser(myUser, false, false));

        return findMarkingInfoResponseDtoList(true, myUser,
            markingQueryDtoSet);
    }


    /**
     * 내 마킹 리스트 출력 (후에 타 사용자 마킹리스트 출력 업데이트 될 예정)
     *
     * @param nickname
     * @return
     */
    public MyMarkingsResponseDto findAllMarkersByUser(String nickname) {
        User myUser = userQueryService.findCurrentUser();
        User profileUser = userQueryService.findByNickname(nickname).orElse(null);
        if (profileUser == null) {
            throw new ResourceNotFoundException("error.notfound.user");
        }

        boolean isMyProfile = myUser.getEmail().equals(profileUser.getEmail());

        // 임시 저장 갯수를 위해 검색
        Set<MarkingQueryDto> tempMarkingList = markingQueryService.findAllMarkersByUser(profileUser, false, true);

        Set<MarkingQueryDto> markingQueryDtoSet = new HashSet<>();
        if (isMyProfile) {
            markingQueryDtoSet.addAll(markingQueryService.findAllMarkersByUser(myUser, false, false));
        }

        List<MarkingInfoResponseDto> markingInfoResponseDtos = findMarkingInfoResponseDtoList(true, profileUser,
            markingQueryDtoSet);

        return new MyMarkingsResponseDto(isMyProfile, (long) tempMarkingList.size(),
            markingInfoResponseDtos);
    }

    /**
     * 내 임시 마킹 리스트 출력
     *
     * @return
     */
    public MyTempMarkingsResponseDto findTempMarkersByUser() {
        User myUser = userQueryService.findCurrentUser();

        Set<MarkingQueryDto> markingQueryDtoSet = new HashSet<>(
            markingQueryService.findAllMarkersByUser(myUser, false, true));

        List<MarkingInfoResponseDto> markingInfoResponseDtos = findMarkingInfoResponseDtoList(true, myUser,
            markingQueryDtoSet);

        return new MyTempMarkingsResponseDto(markingInfoResponseDtos);
    }

    private List<MarkingInfoResponseDto> findMarkingInfoResponseDtoList(boolean isMember, User currentUser,
        Set<MarkingQueryDto> nearbyMarkers) {
        List<MarkingInfoResponseDto> markingInfoList = new ArrayList<>();

        if (nearbyMarkers.isEmpty()) {
            return markingInfoList;
        }

        Map<Long, MarkingQueryDto> markingMap = nearbyMarkers.stream()
            .collect(Collectors.toMap(key -> key.getMarking().getId(), value -> value));

        Map<Long, LikeCountResponseDto> likeMap = likesService.findLikeCounts(markingMap.keySet(), ContentType.MARKING)
            .stream()
            .collect(Collectors.toMap(LikeCountResponseDto::getContentId, value -> value));

        for (Map.Entry<Long, MarkingQueryDto> entry : markingMap.entrySet()) {
            Long id = entry.getKey();
            MarkingQueryDto nearbyMarker = entry.getValue();
            Marking marking = nearbyMarker.getMarking();
            Pet pet = nearbyMarker.getPet();
            // 한번에 모든 데이터를 설정하여 객체 초기화를 효율적으로 수행
            MarkingInfoResponseDto markingInfoResponseDto ;

            markingInfoResponseDto = findMarkingInfoResponseDto(entry, marking);

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
        return markingInfoList;
    }

    /**
     * value 에 따른 업캐스팅 분류
     * @param entry
     * @param marking
     * @return
     */
    private static MarkingInfoResponseDto findMarkingInfoResponseDto(Entry<Long, MarkingQueryDto> entry,
        Marking marking) {
        MarkingInfoResponseDto markingInfoResponseDto;
        Likes likes = entry.getValue().getLikes();
        MarkingSaves markingSaves = entry.getValue().getMarkingSaves();
        markingInfoResponseDto = new MarkingInfoResponseDto(marking);

        if (likes != null) {
            markingInfoResponseDto.updateLikedInfo(likes.getId(), likes.getRegDt());
        }
        if (markingSaves != null) {
            markingInfoResponseDto.updateSavedInfo(markingSaves.getId(), markingSaves.getRegDt());
        }
        return markingInfoResponseDto;
    }

}
