package com.mungwithme.maps.controller;


import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonResult;
import com.mungwithme.maps.dto.response.GeocodingResponseDto;
import com.mungwithme.maps.service.GoogleReverseGeocodingApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/maps")
public class MapsController {

    private final GoogleReverseGeocodingApiService googleReverseGeocodingApiService;
    private final BaseResponse baseResponse;

    @GetMapping("/reverse-geocode")
    public ResponseEntity reverseGeocode(@RequestParam(name = "lat") double lat, @RequestParam(name = "lng") double lng) throws IOException {
        
      return baseResponse.sendContentResponse(googleReverseGeocodingApiService.getReverseGeocoding(lat, lng), 200);
    }

}
