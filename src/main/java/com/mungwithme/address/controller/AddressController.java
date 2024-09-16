package com.mungwithme.address.controller;


import com.mungwithme.address.model.dto.request.AddressCoordinatesDto;
import com.mungwithme.address.model.dto.request.AddressSearchDto;
import com.mungwithme.address.service.AddressSearchService;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/addresses")
public class AddressController {


    private final AddressSearchService addressSearchService;
    private final BaseResponse baseResponse;



    /**
     * 키워드로 읍면동을 기준으로 동네 검색 API
     *
     * @param addressSearchDto
     * @return
     */
    @GetMapping("")
    public ResponseEntity fetchListBySubDist(@Validated @ModelAttribute AddressSearchDto addressSearchDto) throws IOException {
        try {
            return baseResponse.sendContentResponse(
                    addressSearchService.fetchListBySubDist(addressSearchDto, 0, 7), 200);
        } catch (ResourceNotFoundException e) {
            return baseResponse.sendErrorResponse(204, "입력하신 주소가 없습니다");
        } catch (Exception e) {
            log.error(e.getMessage());
            return baseResponse.sendErrorResponse(500, "예상치 못한 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.");
        }
    }

    /**
     * 사용자 위경도를 이용한 동네 검색 API
     * 총 10km 반경으로 검색
     *
     * @param addressCoordinatesDto
     * @return
     */
    @GetMapping("/search-by-location")
    public ResponseEntity<CommonBaseResult> fetchListByLocation(@ModelAttribute AddressCoordinatesDto addressCoordinatesDto) throws IOException {
        try {
            return baseResponse.sendContentResponse(addressSearchService.fetchListByLngLat(addressCoordinatesDto, 0, 7,10000), 200);
        } catch (ResourceNotFoundException e) {
            return baseResponse.sendErrorResponse(204, "잘못된 위치 정보입니다.");
        } catch (Exception e) {
            log.error(e.getMessage());
            return baseResponse.sendErrorResponse(500, "예상치 못한 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.");
        }
    }

}
