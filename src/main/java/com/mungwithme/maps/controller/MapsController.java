package com.mungwithme.maps.controller;


import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.maps.service.GoogleReverseGeocodingApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/maps")
public class MapsController {

    private final GoogleReverseGeocodingApiService googleReverseGeocodingApiService;
    private final BaseResponse baseResponse;

    @GetMapping("/reverse-geocode")
    public CommonBaseResult reverseGeocode(@RequestParam(name = "lat") double lat, @RequestParam(name = "lng") double lng) {
        
      return baseResponse.getContentResult(googleReverseGeocodingApiService.getReverseGeocoding(lat, lng));
    }

}
