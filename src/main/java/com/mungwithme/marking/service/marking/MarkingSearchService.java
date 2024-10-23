package com.mungwithme.marking.service.marking;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.address.service.AddressQueryService;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.util.GeoUtils;
import com.mungwithme.likes.model.entity.MarkingLikes;
import com.mungwithme.likes.service.MarkingLikesService;
import com.mungwithme.maps.dto.response.LocationBoundsDto;
import com.mungwithme.marking.model.dto.request.MarkingSearchDto;
import com.mungwithme.marking.model.dto.response.MarkingDistWithCountRepDto;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.model.dto.response.MarkingPagingResponseDto;
import com.mungwithme.marking.model.dto.sql.MarkingQueryDto;
import com.mungwithme.marking.model.entity.MarkImage;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.model.entity.MarkingSaves;
import com.mungwithme.marking.model.enums.MapViewMode;
import com.mungwithme.marking.model.enums.SortType;
import com.mungwithme.marking.repository.marking.MarkingQueryDslRepository;
import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserQueryService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final MarkingQueryDslRepository markingQueryDslRepository;
    private final MarkingImageQueryService markingImageQueryService;

    /**
     * 동네 마킹 검색 API
     * 보기 권한에 따른 쿼리 처리`
     *
     * @param markingSearchDto
     * @return
     */
    public MarkingPagingResponseDto findNearbyMarkers(MarkingSearchDto markingSearchDto,
        LocationBoundsDto locationBoundsDto, int offset,
        SortType sortType) {

        int pageSize = 20;
        GeoUtils.checkLocationBoundsDto(locationBoundsDto);

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
     * 내 마킹 리스트 출력
     * <p>
     * 전체 보기,현재위치중심,지도위치중심
     * 인기순,거리순,최신순
     * <p>
     * 나의 프로필 인경우, 타 유저의 프로필 인 경우
     *
     * @param nickname
     * @return
     */
    public MarkingPagingResponseDto findAllMarkersByUser(
        String nickname,
        LocationBoundsDto locationBoundsDto,
        MarkingSearchDto markingSearchDto,
        int offset,
        SortType sortType,
        MapViewMode mapViewMode) {
        int pageSize = 20;

        PageRequest pageRequest = getPageRequest(offset, pageSize);

        User currentUser = userQueryService.findCurrentUser();
        User profileUser = userQueryService.findByNickname(nickname).orElse(null);
        if (profileUser == null) {
            throw new ResourceNotFoundException("error.notfound.user");
        }

        // 자신의 프로필인지 확인
        boolean isMyProfile = currentUser.getEmail().equals(profileUser.getEmail());

        Set<Address> addressSet = null;

        if (!mapViewMode.equals(MapViewMode.ALL_VIEW)) {
            // 좌표 확인
            GeoUtils.checkLocationBoundsDto(locationBoundsDto);
            addressSet = addressQueryService.findAddressInBounds(locationBoundsDto.getSouthBottomLat(),
                locationBoundsDto.getNorthTopLat(),
                locationBoundsDto.getSouthLeftLng(), locationBoundsDto.getNorthRightLng());

            // 주소가 없는 경우
            if (addressSet.isEmpty()) {
                throw new ResourceNotFoundException("error.notfound.coordinates");
            }

        }

        Long tempCount = null;
        // 임시 저장 갯수를 위해 검색
        if (isMyProfile) {
            tempCount = markingQueryService.findTempCount(profileUser, false, true);
        }

        Page<MarkingQueryDto> pageDto = null;

        pageDto = markingQueryDslRepository.findAllMarkersByUser(
            addressSet,
            markingSearchDto,
            false,
            false,
            currentUser,
            profileUser,
            pageRequest,
            sortType,
            mapViewMode,
            isMyProfile
        );

        Set<MarkingQueryDto> nearbyMarkers = new HashSet<>(pageDto.getContent());

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

    private static PageRequest getPageRequest(int offset, int pageSize) {
        return PageRequest.of(offset, pageSize);
    }

    /**
     * 나의 좋아요 마킹 리스트 출력 API
     */
    public MarkingPagingResponseDto findAllLikedMarkersByUser(int offset) {
        User myUser = userQueryService.findCurrentUser();

        PageRequest pageRequest = getPageRequest(offset, 20);

        Page<MarkingQueryDto> pageDto = markingQueryService.findAllLikedMarkersByUser(myUser, false,
            false, pageRequest);

        Set<MarkingQueryDto> likesMarkers = new HashSet<>(pageDto.getContent());
        List<MarkingInfoResponseDto> markingInfoResponseDtos = setMarkingInfoResponseDtoList(true, myUser,
            likesMarkers);

        return MarkingPagingResponseDto.builder()
            .markings(markingInfoResponseDtos)
            .totalElements(pageDto.getTotalElements())
            .totalPages(pageDto.getTotalPages())
            .pageable(pageDto.getPageable())
            .build();


    }

    /**
     * 나의 즐겨찾기 마킹 리스트 출력 API
     */
    public MarkingPagingResponseDto findAllSavedMarkersByUser(int offset) {
        User myUser = userQueryService.findCurrentUser();
        PageRequest pageRequest = getPageRequest(offset, 20);

        Page<MarkingQueryDto> pageDto = markingQueryService.findAllSavedMarkersByUser(myUser, false,
            false, pageRequest);

        Set<MarkingQueryDto> savesMarkers = new HashSet<>(pageDto.getContent());
        List<MarkingInfoResponseDto> markingInfoResponseDtos = setMarkingInfoResponseDtoList(true, myUser,
            savesMarkers);
        return MarkingPagingResponseDto.builder()
            .markings(markingInfoResponseDtos)
            .totalElements(pageDto.getTotalElements())
            .totalPages(pageDto.getTotalPages())
            .pageable(pageDto.getPageable())
            .build();
    }

    /**
     * 읍면동 별 마커 갯수 가져오기
     *
     * @param locationBoundsDto
     */
    public List<MarkingDistWithCountRepDto> findCountBySubDistrict(LocationBoundsDto locationBoundsDto) {

        GeoUtils.checkLocationBoundsDto(locationBoundsDto);

        User currentUser = userQueryService.findCurrentUser_v2();

        Set<Address> addressSet = addressQueryService.findAddressInBounds(locationBoundsDto.getSouthBottomLat(),
            locationBoundsDto.getNorthTopLat(),
            locationBoundsDto.getSouthLeftLng(), locationBoundsDto.getNorthRightLng());

        if (addressSet.isEmpty()) {
            throw new ResourceNotFoundException("error.notfound.coordinates");
        }
        List<MarkingDistWithCountRepDto> withDtoList = new ArrayList<>();

        Map<Long, Address> addressMap = addressSet.stream().collect(Collectors.toMap(Address::getId, value -> value));

        List<MarkingQueryDto> countBySubDistrict = markingQueryDslRepository.findCountBySubDistrict(currentUser,
            addressSet);

        for (MarkingQueryDto markingQueryDto : countBySubDistrict) {
            Address address = addressMap.get(markingQueryDto.getAddressId());

            MarkingDistWithCountRepDto markingDistWithCountRepDto = new MarkingDistWithCountRepDto(
                address, markingQueryDto.getTotalCount()
            );
            withDtoList.add(markingDistWithCountRepDto);
        }

        return withDtoList;
    }


    public void findMarkByBounds(LocationBoundsDto locationBoundsDto) {
        GeoUtils.checkLocationBoundsDto(locationBoundsDto);

        User currentUser = userQueryService.findCurrentUser_v2();




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
            markingQueryDtoSet);

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

        Map<Long, List<MarkImage>> markImageMap = null;
        List<MarkImage> markImages = markingImageQueryService.findAllByMarkingIds(markingMap.keySet());

        if (!markImages.isEmpty()) {
            markImageMap = markImages.stream()
                .collect(Collectors.groupingBy(key -> key.getMarking().getId(), Collectors.toList()));
        }


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

            if (markImageMap != null) {
                List<MarkImage> markImageList = markImageMap.get(id);

                markingInfoResponseDto.updateImage(markImageList);
            }


            // Pet 정보 업데이트
            markingInfoResponseDto.updatePet(pet);

            // 좋아요 수 및
            markingInfoResponseDto.updateLikeCount(markingQueryDto.getLikeCount());
            markingInfoResponseDto.updateSaveCount(markingQueryDto.getSaveCount());

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
