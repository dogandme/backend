package com.mungwithme.marking.controller;


import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.maps.dto.response.LocationBoundsDTO;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.service.marking.MarkingSearchService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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


    @GetMapping
    public ResponseEntity<CommonBaseResult> fetchMarkingById(
        @RequestBody @Validated LocationBoundsDTO locationBoundsDTO)
        throws IOException {
        try {
            List<MarkingInfoResponseDto> nearbyMarkers = markingSearchService.findNearbyMarkers(locationBoundsDTO);
            return baseResponse.sendContentResponse(nearbyMarkers, 200);
        } catch ( ResourceNotFoundException | IllegalArgumentException e) {
            return baseResponse.sendErrorResponse(400, "ex) 잘못된 위치정보입니다.");
        }
    }


}
