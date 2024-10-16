package com.mungwithme.marking.service.marking;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.address.service.AddressQueryService;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.util.GeoUtils;
import com.mungwithme.likes.model.entity.MarkingLikes;
import com.mungwithme.likes.service.MarkingLikesService;
import com.mungwithme.maps.dto.response.LocationBoundsDto;
import com.mungwithme.marking.model.dto.request.MarkingSearchDto;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.model.dto.response.MarkingPagingResponseDto;
import com.mungwithme.marking.model.dto.response.MyMarkingsResponseDto;
import com.mungwithme.marking.model.dto.response.MyTempMarkingsResponseDto;
import com.mungwithme.marking.model.dto.sql.MarkingQueryDto;
import com.mungwithme.marking.model.entity.MarkImage;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.model.entity.MarkingSaves;
import com.mungwithme.marking.model.enums.SortType;
import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserQueryService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MarkingSearchService {

    private final AddressQueryService addressQueryService;
    private final UserQueryService userQueryService;
    private final MarkingQueryService markingQueryService;
    private final MarkingLikesService likesService;
    private final MarkingImageQueryService markingImageQueryService;

    /**
     * 동네 마킹 검색 API
     * 보기 권한에 따른 쿼리 처리`
     *
     * @param markingSearchDto
     * @return
     */
    public MarkingPagingResponseDto findNearbyMarkers(MarkingSearchDto markingSearchDto, LocationBoundsDto locationBoundsDto, int offset,
        SortType sortType) {

        int pageSize = 20;
        GeoUtils.isWithinKorea(locationBoundsDto.getNorthTopLat(),
            locationBoundsDto.getNorthRightLng());
        GeoUtils.isWithinKorea(locationBoundsDto.getSouthBottomLat(),
            locationBoundsDto.getSouthLeftLng());

        Set<MarkingQueryDto> nearbyMarkers = new HashSet<>();
        User currentUser = userQueryService.findCurrentUser_v2();

        boolean isMember = currentUser != null;

        // 주소 검색
        Set<Address> addressSet = addressQueryService.findAddressInBounds(locationBoundsDto.getSouthBottomLat(),
            locationBoundsDto.getNorthTopLat(),
            locationBoundsDto.getSouthLeftLng(), locationBoundsDto.getNorthRightLng());

        if (addressSet.isEmpty()) {
            throw new ResourceNotFoundException("error.notfound.markings");
        }

        Page<MarkingQueryDto> pageDto = null;
        // 회원
        if (isMember) {
            pageDto = markingQueryService.findNearbyMarkers(currentUser, addressSet,
                markingSearchDto.getLat(),
                markingSearchDto.getLng(), sortType, false, false, offset, pageSize);
            nearbyMarkers.addAll(pageDto.getContent());

            // 비 회원
        } else {
            pageDto = markingQueryService.findNearbyMarkers(addressSet, markingSearchDto.getLat(),
                markingSearchDto.getLng(), sortType, false, false, offset, pageSize);
            nearbyMarkers.addAll(pageDto.getContent());
        }

        List<MarkingInfoResponseDto> markingInfoResponseDtos = setMarkingInfoResponseDtoList(isMember, currentUser,
            nearbyMarkers);

        return new MarkingPagingResponseDto(markingInfoResponseDtos, pageDto.getTotalElements(),
            pageDto.getTotalPages(), pageDto.getPageable());
    }


    /**
     * 내 마킹 리스트 출력 (후에 타 사용자 마킹리스트 출력 업데이트 될 예정)
     *
     * @param nickname
     * @return
     */
    public MarkingPagingResponseDto findAllMarkersByUser(String nickname, MarkingSearchDto markingSearchDto, int offset,
        SortType sortType) {
        int pageSize = 20;

        User myUser = userQueryService.findCurrentUser();
        User profileUser = userQueryService.findByNickname(nickname).orElse(null);
        if (profileUser == null) {
            throw new ResourceNotFoundException("error.notfound.user");
        }

        Set<MarkingQueryDto> nearbyMarkers = new HashSet<>();
        boolean isMyProfile = myUser.getEmail().equals(profileUser.getEmail());

        // 임시 저장 갯수를 위해 검색
        long tempCount = markingQueryService.findTempCount(profileUser, false, true);

        Page<MarkingQueryDto> pageDto = null;
        if (isMyProfile) {
            pageDto = markingQueryService.findAllMarkersByUser(markingSearchDto.getLat(),
                markingSearchDto.getLng()
                , myUser, false, false, offset, pageSize, sortType);
            nearbyMarkers.addAll(pageDto.getContent());
        }

        List<MarkingInfoResponseDto> markingInfoResponseDtos = setMarkingInfoResponseDtoList(true, profileUser,
            nearbyMarkers);

        return MarkingPagingResponseDto.builder()
            .markings(markingInfoResponseDtos)
            .isMyProfile(isMyProfile)
            .tempCount(tempCount)
            .totalElements(pageDto.getTotalElements())
            .totalPages(pageDto.getTotalPages())
            .pageable(pageDto.getPageable())
            .build();
    }

    /**
     * 나의 좋아요 마킹 리스트 출력 API
     */
    public List<MarkingInfoResponseDto> findAllLikedMarkersByUser() {
        User myUser = userQueryService.findCurrentUser();

        Set<MarkingQueryDto> markingQueryDtoSet = new HashSet<>(
            markingQueryService.findAllLikedMarkersByUser(myUser, false, false));

        return setMarkingInfoResponseDtoList(true, myUser,
            markingQueryDtoSet);
    }

    /**
     * 나의 즐겨찾기 마킹 리스트 출력 API
     */
    public List<MarkingInfoResponseDto> findAllSavedMarkersByUser() {
        User myUser = userQueryService.findCurrentUser();

        Set<MarkingQueryDto> markingQueryDtoSet = new HashSet<>(
            markingQueryService.findAllSavedMarkersByUser(myUser, false, false));

        return setMarkingInfoResponseDtoList(true, myUser,
            markingQueryDtoSet);
    }


    /**
     * 내 임시 마킹 리스트 출력
     *
     * @return
     */
    public MarkingPagingResponseDto findTempMarkersByUser(int offset) {

        int pageSize = 20;

        User myUser = userQueryService.findCurrentUser();

        Page<MarkingQueryDto> pageDto = markingQueryService.findAllMarkersByUser(0.0, 0.0, myUser, false, true,
            offset, pageSize, SortType.RECENT);

        Set<MarkingQueryDto> markingQueryDtoSet = new HashSet<>(pageDto.getContent());
        List<MarkingInfoResponseDto> markingInfoResponseDtos = setMarkingInfoResponseDtoList(true, myUser,
            markingQueryDtoSet );

        return MarkingPagingResponseDto.builder()
            .markings(markingInfoResponseDtos)
            .totalElements(pageDto.getTotalElements())
            .totalPages(pageDto.getTotalPages())
            .pageable(pageDto.getPageable())
            .build();
    }



    private List<MarkingInfoResponseDto> setMarkingInfoResponseDtoList(boolean isMember, User currentUser,
        Set<MarkingQueryDto> nearbyMarkers) {
        List<MarkingInfoResponseDto> markingInfoList = new ArrayList<>();

        if (nearbyMarkers.isEmpty()) {
            return markingInfoList;
        }

        Map<Long, MarkingQueryDto> markingMap = nearbyMarkers.stream()
            .collect(Collectors.toMap(key -> key.getMarking().getId(), value -> value));

        List<MarkImage> markImages = markingImageQueryService.findAllByMarkingIds(markingMap.keySet());

        Map<Long, List<MarkImage>> markImageMap = markImages.stream()
            .collect(Collectors.groupingBy(key -> key.getMarking().getId(), Collectors.toList()));

        for (Map.Entry<Long, MarkingQueryDto> entry : markingMap.entrySet()) {
            Long id = entry.getKey();

            MarkingQueryDto markingQueryDto = entry.getValue();
            Marking marking = markingQueryDto.getMarking();
            Pet pet = markingQueryDto.getPet();
            // 한번에 모든 데이터를 설정하여 객체 초기화를 효율적으로 수행
            MarkingInfoResponseDto markingInfoResponseDto;

            //
            markingInfoResponseDto = createMarkingInfoResponseDto(entry, marking);

            // 작성자인지 확인
            if (isMember) {
                markingInfoResponseDto.updateIsOwner(currentUser.getEmail().equals(marking.getUser().getEmail()));
            }

            // 따로 가져온 이미지 리스트 업데이트
            List<MarkImage> markImageList = markImageMap.get(id);
            markingInfoResponseDto.updateImage(markImageList);

            // Pet 정보 업데이트
            markingInfoResponseDto.updatePet(pet);

            // 좋아요 수 및
            markingInfoResponseDto.updateLikeCount(markingQueryDto.getLikeCount());
            markingInfoResponseDto.updateLikeCount(markingQueryDto.getSaveCount());

            markingInfoList.add(markingInfoResponseDto);
        }
        return markingInfoList;
    }

    /**
     * value 에 따른 업캐스팅 분류
     *
     * @param entry
     * @param marking
     * @return
     */
    private static MarkingInfoResponseDto createMarkingInfoResponseDto(Entry<Long, MarkingQueryDto> entry,
        Marking marking) {
        MarkingInfoResponseDto markingInfoResponseDto;
        MarkingLikes likes = entry.getValue().getMarkingLikes();
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
