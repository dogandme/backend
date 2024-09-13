package com.mungwithme.maps.service;

import com.mungwithme.common.util.GeoUtils;
import com.mungwithme.maps.dto.response.GeocodingResponseDto;
import com.mungwithme.maps.dto.response.GoogleGeocodingResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Google Reverse Geocoding API를 호출하는 Service 구현 (WebClient 사용)
 * <p>
 * 비동기 처리: WebClient 는 비동기식으로 HTTP 요청을 처리하여 성능 최적화가 가능
 */
@Slf4j
@Service
public class GoogleReverseGeocodingApiService {


    private final String API_KEY; // Google API Key

    private static final String GEOCODING_API_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    private final RestTemplate restTemplate;

    public GoogleReverseGeocodingApiService(@Value("${google.api.key}") String apiKey) {
        this.API_KEY = apiKey;
        this.restTemplate = new RestTemplate();
    }

    public GeocodingResponseDto getReverseGeocoding(double lat, double lng) {
        boolean withinKorea = GeoUtils.isWithinKorea(lat, lng);
        if (!withinKorea) {
            throw new IllegalArgumentException("ex) 위치가 정확하지 않습니다.");
        }

        String url = GEOCODING_API_URL + "?latlng=" + lat + "," + lng + "&key=" + API_KEY + "&language=ko";
        // Google Geocoding API 요청 보내기
        GoogleGeocodingResponseDto response = restTemplate.getForObject(url, GoogleGeocodingResponseDto.class);
        if (response != null && !response.getResults().isEmpty()) {
            return new GeocodingResponseDto(response.getResults().get(0).getFormattedAddress());
        }
        return null;
    }


}
