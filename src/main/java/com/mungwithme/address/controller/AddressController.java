package com.mungwithme.address.controller;


import com.mungwithme.address.model.dto.request.AddressSearchDto;
import com.mungwithme.address.service.AddressSearchService;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public CommonBaseResult fetchListBySubDist(@ModelAttribute AddressSearchDto addressSearchDto) {
        try {
            return baseResponse.getContentResult(addressSearchService.fetchListBySubDist(addressSearchDto, 0, 7));
        } catch (ResourceNotFoundException e) {
            return baseResponse.getFailResult(204, "입력하신 주소가 없습니다");
        } catch (Exception e) {
            log.error(e.getMessage());
            return baseResponse.getFailResult(400, "error");
        }
    }

}
