package com.mungwithme.marking.controller;


import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.maps.dto.response.LocationBoundsDTO;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.model.dto.response.MyMarkingsResponseDto;
import com.mungwithme.marking.model.dto.response.MyTempMarkingsResponseDto;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
     * 주변 마킹 검색 API
     * 비회원 식별 후 검색 기능을 다르게
     * 보기 권한에 따른 쿼리
     *
     * @param locationBoundsDTO
     * @return
     */
//    @NoneAuthorize
    @GetMapping
    public ResponseEntity<CommonBaseResult> getMarkingsById(@ModelAttribute @Validated LocationBoundsDTO locationBoundsDTO)
        throws IOException {
        List<MarkingInfoResponseDto> nearbyMarkers = markingSearchService.findNearbyMarkers(locationBoundsDTO);
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
    public ResponseEntity<CommonBaseResult> getMyMarkingsByUser(@PathVariable(name = "nickname") String nickname)
        throws IOException {
        MyMarkingsResponseDto markingsResponseDto = markingSearchService.findAllMarkersByUser(nickname);
        return baseResponse.sendContentResponse(markingsResponseDto, HttpStatus.OK.value());

    }

    /**
     * 내 임시 마킹 리스트 출력
     *
     * @return
     */
    @GetMapping("/temp")
    public ResponseEntity<CommonBaseResult> getMyTempMarkingsByUser()
        throws IOException {
        MyTempMarkingsResponseDto tempMarkersByUser = markingSearchService.findTempMarkersByUser();
        return baseResponse.sendContentResponse(tempMarkersByUser, HttpStatus.OK.value());

    }

    /**
     * 좋아요 날짜
     */

    /**
     * 내가 좋아요한 마킹 리스트 불러오기
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
     * 좋아요 날짜
     */

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
