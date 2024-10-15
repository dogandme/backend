package com.mungwithme.marking.controller;


import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.maps.dto.response.LocationBoundsDto;
import com.mungwithme.marking.model.dto.request.MarkingSearchDto;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.model.dto.response.MarkingPagingResponseDto;
import com.mungwithme.marking.model.enums.SortType;
import com.mungwithme.marking.service.marking.MarkingSearchService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/markings/search")
public class MarkingSearchController {


    private final MarkingSearchService markingSearchService;
    private final BaseResponse baseResponse;

    /**
     * 내 위치 중심
     * 현재 지도 중심
     *
     *
     * 인기순
     * 최신순
     * 가까운순
     *
     */

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
    @GetMapping
    public ResponseEntity<CommonBaseResult> getMarkingsById(
        @ModelAttribute @Validated MarkingSearchDto markingSearchDto,
        @ModelAttribute @Validated LocationBoundsDto locationBoundsDto,
        @RequestParam(value = "offset", defaultValue = "0") int offset,
        @RequestParam(value = "sortType", defaultValue = "POPULARITY") SortType sortType

    )
        throws IOException {
        MarkingPagingResponseDto nearbyMarkers = markingSearchService.findNearbyMarkers(markingSearchDto,locationBoundsDto, offset,
            sortType);

        if (nearbyMarkers.getMarkings().isEmpty()) {
            return baseResponse.sendNoContentResponse();
        }
        return baseResponse.sendContentResponse(nearbyMarkers, HttpStatus.OK.value());
    }

    /**
     * 내 마킹 리스트 출력 (후에 타 사용자 마킹리스트 출력 업데이트 될 예정)
     *
     * @param nickname
     *     닉네임으로 검색 추후 변경할수도 있음
     * @return
     */
    @GetMapping("/{nickname}")
    public ResponseEntity<CommonBaseResult> getMyMarkingsByUser(@PathVariable(name = "nickname") String nickname,
        @ModelAttribute @Validated MarkingSearchDto markingSearchDto,
        @ModelAttribute @Validated LocationBoundsDto locationBoundsDto,
        @RequestParam(value = "offset", defaultValue = "0") int offset,
        @RequestParam(value = "sortType", defaultValue = "POPULARITY") SortType sortType)
        throws IOException {
        MarkingPagingResponseDto allMarkersByUser = markingSearchService.findAllMarkersByUser(nickname,
            markingSearchDto, offset, sortType);
        if (allMarkersByUser.getMarkings().isEmpty()) {
            return baseResponse.sendNoContentResponse();
        }
        return baseResponse.sendContentResponse(allMarkersByUser, HttpStatus.OK.value());

    }

    /**
     * 내 임시 마킹 리스트 출력
     *
     * @return
     */
    @GetMapping("/temp")
    public ResponseEntity<CommonBaseResult> getMyTempMarkingsByUser(
        @RequestParam(value = "offset", defaultValue = "0") int offset)
        throws IOException {
        MarkingPagingResponseDto tempMarkersByUser = markingSearchService.findTempMarkersByUser(offset);
        if (tempMarkersByUser.getMarkings().isEmpty()) {
            return baseResponse.sendNoContentResponse();
        }
        return baseResponse.sendContentResponse(tempMarkersByUser, HttpStatus.OK.value());

    }

    /**
     * 좋아요 날짜
     *
     * @return
     */
    @GetMapping("/likes")
    public ResponseEntity<CommonBaseResult> getMyLikedMarkingsByUser()
        throws IOException {
        List<MarkingInfoResponseDto> likedMarkersByUser = markingSearchService.findAllLikedMarkersByUser();
        return baseResponse.sendContentResponse(likedMarkersByUser, HttpStatus.OK.value());

    }

    /**
     * 내가 즐겨 찾기 한 마킹 리스트 불러오기
     *
     * @return
     */
    @GetMapping("/saves")
    public ResponseEntity<CommonBaseResult> getMySavedMarkingsByUser()
        throws IOException {
        List<MarkingInfoResponseDto> savedMarkersByUser = markingSearchService.findAllSavedMarkersByUser();
        return baseResponse.sendContentResponse(savedMarkersByUser, HttpStatus.OK.value());
    }

}
