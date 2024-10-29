package com.mungwithme.marking.controller;


import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.maps.dto.response.LocationBoundsDto;
import com.mungwithme.marking.model.dto.request.MarkingSearchDto;
import com.mungwithme.marking.model.dto.response.MarkPagingRepDto;
import com.mungwithme.marking.model.dto.response.MarkRepDto;
import com.mungwithme.marking.model.dto.response.MarkingDistWithCountRepDto;
import com.mungwithme.marking.model.dto.response.MarkingPagingResponseDto;
import com.mungwithme.marking.model.enums.MapViewMode;
import com.mungwithme.marking.model.enums.SortType;
import com.mungwithme.marking.service.marking.MarkingSearchService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 마킹 검색 API
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/markings")
public class MarkingSearchController {


    private final MarkingSearchService markingSearchService;
    private final BaseResponse baseResponse;

    /**
     * 인기순, 최신순, 가까운순
     * 동네 마킹 검색 API
     * 비회원 식별 후 검색 기능을 다르게
     * 보기 권한에 따른 쿼리
     *
     * @param markingSearchDto
     * @return
     */
//    @NoneAuthorize
    @GetMapping("/nearby")
    public ResponseEntity<CommonBaseResult> getNearbyMarkingsById(
        @ModelAttribute MarkingSearchDto markingSearchDto,
        @ModelAttribute LocationBoundsDto locationBoundsDto,
        @RequestParam(value = "offset", defaultValue = "0") int offset,
        @RequestParam(value = "sortType", defaultValue = "POPULARITY") SortType sortType

    )
        throws IOException {
        MarkingPagingResponseDto nearbyMarkers = markingSearchService.findNearbyMarkers(markingSearchDto,
            locationBoundsDto, offset,
            sortType);
        return baseResponse.sendContentResponse(nearbyMarkers, HttpStatus.OK.value());
    }

    /**
     *
     * 이 장소 및 동네 마킹 리스트 불러오기
     */
    @GetMapping("/bounds")
    public ResponseEntity<CommonBaseResult> getLocationMarkingsById(
        @ModelAttribute MarkingSearchDto markingSearchDto,
        @ModelAttribute LocationBoundsDto locationBoundsDto,
        @RequestParam(value = "offset", defaultValue = "0") int offset,
        @RequestParam(value = "sortType", defaultValue = "POPULARITY") SortType sortType
    )
        throws IOException {
        MarkingPagingResponseDto locationMarkings = markingSearchService.findMarkingsByBounds(markingSearchDto,
            locationBoundsDto, offset,
            sortType);
        return baseResponse.sendContentResponse(locationMarkings, HttpStatus.OK.value());
    }




    /**
     * 유저 마킹 리스트 출력
     * <p>
     * 전체 보기,현재위치중심,지도위치중심
     * 인기순,거리순,최신순
     * <p>
     * 나의 프로필 인경우, 타 유저의 프로필 인 경우
     *
     * @param nickname
     *     닉네임으로 검색 추후 변경할수도 있음
     * @return
     */
    @GetMapping("/users/{nickname}")
    public ResponseEntity<CommonBaseResult> getMyMarkingsByUser(@PathVariable(name = "nickname") String nickname,
        @ModelAttribute MarkingSearchDto markingSearchDto,
        @ModelAttribute LocationBoundsDto locationBoundsDto,
        @RequestParam(value = "offset", defaultValue = "0") int offset,
        @RequestParam(value = "sortType", defaultValue = "POPULARITY") SortType sortType,
        @RequestParam(value = "mapViewMode", defaultValue = "ALL_VIEW") MapViewMode mapViewMode
    )
        throws IOException {
        MarkingPagingResponseDto allMarkersByUser = markingSearchService.findAllMarkersByUser(
            nickname,
            locationBoundsDto,
            markingSearchDto, offset, sortType, mapViewMode);
        return baseResponse.sendContentResponse(allMarkersByUser, HttpStatus.OK.value());

    }

    /**
     * 내 임시 마킹 리스트 출력
     *
     * @return
     */
    @GetMapping("/temps")
    public ResponseEntity<CommonBaseResult> getMyTempMarkingsByUser(
        @RequestParam(value = "offset", defaultValue = "0") int offset)
        throws IOException {
        MarkingPagingResponseDto tempMarkersByUser = markingSearchService.findTempMarkersByUser(offset);
        return baseResponse.sendContentResponse(tempMarkersByUser, HttpStatus.OK.value());

    }

    /**
     * 좋아요 날짜
     *
     * @return
     */
    @GetMapping("/likes")
    public ResponseEntity<CommonBaseResult> getMyLikedMarkingsByUser(
        @RequestParam(value = "offset", defaultValue = "0") int offset
    )
        throws IOException {
        MarkingPagingResponseDto likedMarkersByUser = markingSearchService.findAllLikedMarkersByUser(offset);
        return baseResponse.sendContentResponse(likedMarkersByUser, HttpStatus.OK.value());

    }

    /**
     * 내가 즐겨 찾기 한 마킹 리스트 불러오기
     *
     * @return
     */
    @GetMapping("/saves")
    public ResponseEntity<CommonBaseResult> getMySavedMarkingsByUser(
        @RequestParam(value = "offset", defaultValue = "0") int offset
    )
        throws IOException {
        MarkingPagingResponseDto savedMarkersByUser = markingSearchService.findAllSavedMarkersByUser(offset);
        return baseResponse.sendContentResponse(savedMarkersByUser, HttpStatus.OK.value());
    }

    /**
     * 읍면동 별 마커 갯수 출력
     *
     * @param locationBoundsDto
     * @return
     * @throws IOException
     */
    @GetMapping("/district/count")
    public ResponseEntity<CommonBaseResult> getCountBySubDistrict(
        @ModelAttribute LocationBoundsDto locationBoundsDto
    ) throws IOException {

        List<MarkingDistWithCountRepDto> countBySubDistrict = markingSearchService.findCountBySubDistrict(
            locationBoundsDto);
        return baseResponse.sendContentResponse(countBySubDistrict, HttpStatus.OK.value());
    }
    /**
     *
     *  바운더리 내에 마커 불러오기
     *
     *
     */
    @GetMapping("/marks")
    public ResponseEntity<CommonBaseResult> getMarksByBounds(
        @ModelAttribute LocationBoundsDto locationBoundsDto
    ) throws IOException {

        List<MarkRepDto> markByBound = markingSearchService.findMarksByBound(
            locationBoundsDto);
        return baseResponse.sendContentResponse(markByBound, HttpStatus.OK.value());
    }

    /**
     *
     *  나의 마커 전체 불러오기
     *
     *
     */
    @GetMapping("/my-marks")
    public ResponseEntity<CommonBaseResult> getMyMarksByBounds(
        @ModelAttribute LocationBoundsDto locationBoundsDto
    ) throws IOException {

        List<MarkRepDto> markByBound = markingSearchService.findMyMarksByBound();

        return baseResponse.sendContentResponse(markByBound, HttpStatus.OK.value());
    }

    /**
     *
     *  닉네임 해당되는 마커 출력 API
     *
     *
     */
    @GetMapping("/marks/{nickname}")
    public ResponseEntity<CommonBaseResult> getMarksByNickname(
        @RequestParam(value = "offset", defaultValue = "0") int offset,
        @PathVariable(name = "nickname") String nickname
    ) throws IOException {

        MarkPagingRepDto markPagingRepDto = markingSearchService.findAllMarksByUser(nickname, offset);
        return baseResponse.sendContentResponse(markPagingRepDto, HttpStatus.OK.value());
    }



}
