package com.mungwithme.common.util.google.maps;

import com.mungwithme.maps.dto.response.GeocodingResponseDto;
import com.mungwithme.maps.service.GoogleApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

@SpringBootTest
class ReverseGeocodingApiTest {


    @Autowired
    GoogleApiService reverseGeocodingApi;
    @Test
    void findReverseGeocoding() {
        GeocodingResponseDto reverseGeocoding = reverseGeocodingApi.findReverseGeocoding(35.5388, 129.3369);
        // 결과를 block()으로 동기적으로 받아오기

        String region = reverseGeocoding.getRegion();

        System.out.println("region = " + region);


    }


    @Test
    void findReverseGeocoding2() {

        double lat = 35.5388450644851;


        System.out.println("lat = " + (Math.round(lat * 10000)/10000f)) ;

        double lng = 129.3369218488095;

        long round = Math.round(lat);


    }

}