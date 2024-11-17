package com.mungwithme.address.controller;


import com.mungwithme.address.model.dto.request.AddressCoordinatesDto;
import com.mungwithme.address.model.dto.request.AddressSearchDto;
import com.mungwithme.address.service.AddressQueryService;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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


    private final AddressQueryService addressSearchService;
    private final BaseResponse baseResponse;


    /**
     * 키워드로 읍면동을기준으로 동네 검색 API
     *
     * @param addressSearchDto
     * @return
     */
    @GetMapping
    public ResponseEntity<CommonBaseResult> getListBySubDist(
        @Validated @ModelAttribute AddressSearchDto addressSearchDto) throws IOException {

        log.info("addressSearchDto.getKeyword() = {}", addressSearchDto.getKeyword());
        log.debug("addressSearchDto.getKeyword() = {} ", addressSearchDto.getKeyword());
        
        return baseResponse.sendContentResponse(
            addressSearchService.findListBySubDist(addressSearchDto, 0, 7), HttpStatus.OK.value());

    }

    /**
     * 사용자 위경도를 이용한 동네 검색 API
     * 총 10km 반경으로 검색
     *
     * @param addressCoordinatesDto
     * @return
     */
    @GetMapping("/search-by-location")
    public ResponseEntity<CommonBaseResult> getListByLocation(
        @ModelAttribute AddressCoordinatesDto addressCoordinatesDto) throws IOException {
        return baseResponse.sendContentResponse(
            addressSearchService.findListByLngLat(addressCoordinatesDto, 0, 7, 10000),  HttpStatus.OK.value());

    }

}
