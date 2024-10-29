package com.mungwithme.marking.service.marking;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.address.service.AddressQueryService;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.util.GeoUtils;
import com.mungwithme.likes.model.entity.MarkingLikes;
import com.mungwithme.maps.dto.response.LocationBoundsDto;
import com.mungwithme.marking.model.dto.request.MarkingSearchDto;
import com.mungwithme.marking.model.dto.response.MarkPagingRepDto;
import com.mungwithme.marking.model.dto.response.MarkRepDto;
import com.mungwithme.marking.model.dto.response.MarkingDistWithCountRepDto;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.model.dto.response.MarkingPagingResponseDto;
import com.mungwithme.marking.model.dto.response.MarkingPagingResponseDto.MarkingPagingResponseDtoBuilder;
import com.mungwithme.marking.model.dto.sql.MarkingQueryDto;
import com.mungwithme.marking.model.entity.MarkImage;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.model.entity.MarkingSaves;
import com.mungwithme.marking.model.enums.MapViewMode;
import com.mungwithme.marking.model.enums.SortType;
import com.mungwithme.marking.repository.marking.MarkingQueryDslRepository;
import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserFollowService;
import com.mungwithme.user.service.UserQueryService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
    private final MarkingQueryDslRepository markingQueryDslRepository;
    private final MarkingImageQueryService markingImageQueryService;
    private final UserFollowService userFollowService;

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

        User currentUser = userQueryService.findCurrentUser_v2();

        boolean isMember = currentUser != null;

        // 주소 검색
        Set<Address> addressSet = addressQueryService.findAddressInBounds(locationBoundsDto.getSouthBottomLat(),
            locationBoundsDto.getNorthTopLat(),
            locationBoundsDto.getSouthLeftLng(), locationBoundsDto.getNorthRightLng());

        if (addressSet.isEmpty()) {
            throw new ResourceNotFoundException("error.notfound.markings");
        }

        PageRequest pageRequest = getPageRequest(offset, pageSize);

        Page<MarkingQueryDto> pageDto = markingQueryDslRepository.findNearbyMarkers(
            currentUser, addressSet, markingSearchDto, false, false, pageRequest, sortType, isMember
        );

        List<MarkingInfoResponseDto> markingInfoResponseDtos = setMarkingInfoResponseDtoList(isMember, currentUser,
            pageDto.getContent());

        return new MarkingPagingResponseDto(markingInfoResponseDtos, pageDto.getTotalElements(),
            pageDto.getTotalPages(), pageDto.getPageable());
    }

    /**
     * 이 장소 마킹 검색 API
     * 보기 권한에 따른 쿼리 처리`
     */
    public MarkingPagingResponseDto findMarkingsByBounds(MarkingSearchDto markingSearchDto,
        LocationBoundsDto locationBoundsDto, int offset,
        SortType sortType) {
        // 거리순은 포함되지 않음
//        if (sortType.equals(SortType.DISTANCE)) {
//            sortType = SortType.RECENT;
//        }
        int pageSize = 20;

        PageRequest pageRequest = getPageRequest(offset, pageSize);

        GeoUtils.checkLocationBoundsDto(locationBoundsDto);

        User currentUser = userQueryService.findCurrentUser_v2();

        // 멤버인지 확인
        boolean isMember = currentUser != null;

        // 주소 검색
        Page<MarkingQueryDto> pageDto = markingQueryDslRepository.findMarkingsByBounds(false, false,
            currentUser, locationBoundsDto, markingSearchDto, sortType, pageRequest, isMember);

        List<MarkingInfoResponseDto> markingInfoResponseDtos = setMarkingInfoResponseDtoList(isMember,
            currentUser, pageDto.getContent()
        );

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

        boolean isFollowing = false;

        GeoUtils.checkLocationBoundsDto(locationBoundsDto);

        Long tempCount = null;
        // 임시 저장 갯수를 위해 검색
        if (isMyProfile) {
            tempCount = markingQueryDslRepository.findTempCount(profileUser);
        } else {
            isFollowing = !userFollowService.existsFollowing(currentUser, profileUser);
        }

        Page<MarkingQueryDto> pageDto = markingQueryDslRepository.findAllMarkersByUser(
            locationBoundsDto,
            markingSearchDto,
            false,
            false,
            profileUser,
            pageRequest,
            sortType,
            mapViewMode,
            isMyProfile,
            isFollowing
        );

        List<MarkingInfoResponseDto> markingInfoResponseDtos = setMarkingInfoResponseDtoList(true, currentUser,
            pageDto.getContent());

        MarkingPagingResponseDtoBuilder builder = MarkingPagingResponseDto.builder()
            .markings(markingInfoResponseDtos)
            .isMyProfile(isMyProfile)
            .totalElements(pageDto.getTotalElements())
            .totalPages(pageDto.getTotalPages())
            .pageable(pageDto.getPageable());

        if (tempCount != null) {
            builder.tempCount(tempCount);
        }
        return builder.build();
    }

    private static PageRequest getPageRequest(int offset, int pageSize) {
        return PageRequest.of(offset, pageSize);
    }

    /**
     * 나의 좋아요 마킹 리스트 출력 API
     */
    public MarkingPagingResponseDto findAllLikedMarkersByUser(int offset) {
        User currentUser = userQueryService.findCurrentUser();

        PageRequest pageRequest = getPageRequest(offset, 20);

        Page<MarkingQueryDto> pageDto = markingQueryDslRepository.findAllTypeMarkersByUser(false, false,
            currentUser, pageRequest, "like");

        List<MarkingInfoResponseDto> markingInfoResponseDtos = setMarkingInfoResponseDtoList(true, currentUser,
            pageDto.getContent());

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
        User currentUser = userQueryService.findCurrentUser();
        PageRequest pageRequest = getPageRequest(offset, 20);

        Page<MarkingQueryDto> pageDto = markingQueryDslRepository.findAllTypeMarkersByUser(false, false,
            currentUser, pageRequest, "save");

        List<MarkingInfoResponseDto> markingInfoResponseDtos = setMarkingInfoResponseDtoList(true, currentUser,
            pageDto.getContent());
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


    /***
     *
     * 바운더리 안에 있는 마커 불러오기
     *
     * @param locationBoundsDto
     * @return
     */
    public List<MarkRepDto> findMarksByBound(LocationBoundsDto locationBoundsDto) {
        GeoUtils.checkLocationBoundsDto(locationBoundsDto);

        User currentUser = userQueryService.findCurrentUser_v2();

        return markingQueryDslRepository.findMarksByBound(locationBoundsDto, currentUser);
    }

    /***
     *
     * 바운더리 안에 있는 마커 불러오기
     *
     * @return
     */
    public MarkPagingRepDto findAllMarksByUser(String nickname, int offset) {

        PageRequest pageRequest = getPageRequest(offset, 20);

        User currentUser = userQueryService.findCurrentUser();

        User profileUser = null;

        boolean isFollowing = false;

        boolean isMyProfile = currentUser.getNickname().equals(nickname);

        if (!isMyProfile) {
            profileUser = userQueryService.findByNickname(nickname)
                .orElseThrow(() -> new ResourceNotFoundException("error.notfound.user"));

            // true 일경우 팔로우 O, false 인 경우 팔로우 X
            isFollowing = !userFollowService.existsFollowing(currentUser, profileUser);
        } else {
            profileUser = currentUser;
        }

        Page<MarkRepDto> pageDto = markingQueryDslRepository.findAllMarksByUser(false, false, profileUser,
            pageRequest, isMyProfile, isFollowing);

        return new MarkPagingRepDto(pageDto.getContent(), pageDto.getTotalElements(), pageDto.getTotalPages(),
            pageDto.getPageable());
    }


    /***
     *
     * 바운더리 안에 있는 마커 불러오기
     *
     //     * @param locationBoundsDto
     * @return
     */
    public List<MarkRepDto> findMyMarksByBound() {

        User currentUser = userQueryService.findCurrentUser();

        return markingQueryDslRepository.findMyMarksByBound(currentUser);
    }


    /**
     * 내 임시 마킹 리스트 출력
     *
     * @return
     */
    public MarkingPagingResponseDto findTempMarkersByUser(int offset) {

        int pageSize = 20;
        PageRequest pageRequest = getPageRequest(offset, pageSize);
        User currentUser = userQueryService.findCurrentUser();

        Page<MarkingQueryDto> pageDto = markingQueryDslRepository.findTempMarkersByUser(
            false, true, currentUser, pageRequest
        );

        List<MarkingInfoResponseDto> markingInfoResponseDtos = setMarkingInfoResponseDtoList(true, currentUser,
            pageDto.getContent());

        return MarkingPagingResponseDto.builder()
            .markings(markingInfoResponseDtos)
            .totalElements(pageDto.getTotalElements())
            .totalPages(pageDto.getTotalPages())
            .pageable(pageDto.getPageable())
            .build();
    }


    /**
     * 검색한 마킹의 세부정보 저장
     *
     * @param isMember
     * @param currentUser
     * @param markingQueryDtoList
     * @return
     */
    private List<MarkingInfoResponseDto> setMarkingInfoResponseDtoList(boolean isMember, User currentUser,
        List<MarkingQueryDto> markingQueryDtoList) {
        List<MarkingInfoResponseDto> markingInfoList = new ArrayList<>();

        if (markingQueryDtoList.isEmpty()) {
            return markingInfoList;
        }

        Set<Long> ids = markingQueryDtoList.stream().map(marking -> marking.getMarking().getId())
            .collect(Collectors.toSet());

        Map<Long, List<MarkImage>> markImageMap = null;

        // TO DO
        // image 검색
        List<MarkImage> markImages = markingImageQueryService.findAllByMarkingIds(ids);

        if (!markImages.isEmpty()) {
            markImageMap = markImages.stream()
                .collect(Collectors.groupingBy(key -> key.getMarking().getId(), Collectors.toList()));
        }

        for (MarkingQueryDto markingQueryDto : markingQueryDtoList) {
            Long id = markingQueryDto.getMarking().getId();

            Marking marking = markingQueryDto.getMarking();
            Pet pet = markingQueryDto.getPet();
            // 한번에 모든 데이터를 설정하여 객체 초기화를 효율적으로 수행
            MarkingInfoResponseDto markingInfoResponseDto = createMarkingInfoResponseDto(markingQueryDto, marking);

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
     * @param marking
     * @return
     */
    private static MarkingInfoResponseDto createMarkingInfoResponseDto(MarkingQueryDto markingQueryDto,
        Marking marking) {
        MarkingInfoResponseDto markingInfoResponseDto;
        MarkingLikes likes = markingQueryDto.getMarkingLikes();
        MarkingSaves markingSaves = markingQueryDto.getMarkingSaves();
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
