package com.mungwithme.maps.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GoogleApiServiceTest {


    @Autowired
    GoogleApiService googleApiService;
    @Test
    void getReverseGeocoding() {
    }

    @Test
    void getPlaceDetails() {
        double lat = 36.874680;
        double lng =126.587431;
        googleApiService.getPlaceDetails(lat,  lng);
//        googleApiService.getReverseGeocoding(lat, lng);
    }
}