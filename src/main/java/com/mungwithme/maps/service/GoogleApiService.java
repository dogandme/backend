package com.mungwithme.maps.service;

import com.mungwithme.common.util.GeoUtils;
import com.mungwithme.maps.dto.response.GeocodingResponseDto;
import com.mungwithme.maps.dto.response.GoogleGeocodingResponseDto;
import com.mungwithme.maps.dto.response.GoogleGeocodingResponseDto.Result;
import com.mungwithme.maps.dto.response.GooglePlaceResponseDto;
import com.mungwithme.maps.dto.response.PlaceDetailsResponseDto;
import com.mungwithme.maps.dto.response.PlaceResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Google Reverse Geocoding API를 호출하는 Service 구현 (WebClient 사용)
 * Google places API
 * <p>
 * 비동기 처리: WebClient 는 비동기식으로 HTTP 요청을 처리하여 성능 최적화가 가능
 */
@Slf4j
@Service
//@RequiredArgsConstructor
public class GoogleApiService {


//    @Value("${google.api.key}")
    private final String API_KEY; // Google API Key

    private static final String GEOCODING_API_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    // Places API URL
    private static final String PLACES_API_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";


    private final RestTemplate restTemplate;

    public GoogleApiService(@Value("${google.api.key}") String apiKey,RestTemplate restTemplate) {
        this.API_KEY = apiKey;
        this.restTemplate = restTemplate;
    }

    public GeocodingResponseDto findReverseGeocoding(double lat, double lng) {
        GeoUtils.isWithinKorea(lat, lng);

        // Google Geocoding API URL 생성 (언어를 한국어로 설정)
        String geocodingUrl = GEOCODING_API_URL + "?latlng=" + lat + "," + lng + "&key=" + API_KEY + "&language=ko";

        log.info("lat = {}", lat);
        log.info("lng = {}", lng);
        // Google Geocoding API 요청 보내기
        GoogleGeocodingResponseDto response = restTemplate.getForObject(geocodingUrl, GoogleGeocodingResponseDto.class);




        if (response != null && !response.getResults().isEmpty()) {
            List<Result> results = response.getResults();
            log.info(" ===========================================");
            for (Result result : results) {
                String formattedAddress = result.getFormattedAddress();

                log.info("formattedAddress = {}", formattedAddress);

            }
            String formattedAddress = response.getResults().get(0).getFormattedAddress();

            String[] split = formattedAddress.split(" ");

            for (String region : split) {

                log.info("split.length = {}", split.length);

                log.info("region = {}", region);

            }
            if (formattedAddress.startsWith("대한민국 ")) {
                formattedAddress = formattedAddress.replaceFirst("대한민국 ", "");
            }

            return new GeocodingResponseDto(formattedAddress);
        }
        return null;
    }

    public PlaceDetailsResponseDto findPlaceDetails(double lat, double lng) {
        GeoUtils.isWithinKorea(lat, lng);

        // Google Place API로 주변 장소 검색 (nearbysearch 사용)
        String placeUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
            "location=" + lat + "," + lng +
            "&radius=200&type=park&key=" + API_KEY + "&language=ko";

        // Place API 요청 보내기
        GooglePlaceResponseDto placeResponse = restTemplate.getForObject(placeUrl, GooglePlaceResponseDto.class);

        if (placeResponse != null && !placeResponse.getResults().isEmpty()) {
            List<PlaceResult> placeResults = placeResponse.getResults();
            for (PlaceResult place : placeResults) {
                log.info("Place name: {}", place.getName());
                log.info("Place address: {}", place.getFormattedAddress());
                log.info("place.getPlace_id() = {}", place.getPlaceId());
                log.info("place.getPlusCode = {}", place.getPlusCode());
                log.info("place.getVicinity() = {}", place.getVicinity());
                log.info("place.getGeometry() = {}", place.getGeometry().getLocation().getLat());
                log.info("place.getGeometry().getLocation().getLng() = {}", place.getGeometry().getLocation().getLng());

            }

            // 첫 번째 결과를 반환
            PlaceResult firstPlace = placeResults.get(0);
            return new PlaceDetailsResponseDto(firstPlace.getFormattedAddress(), firstPlace.getName(), firstPlace.getGeometry().getLocation().getLat(), firstPlace.getGeometry().getLocation().getLng());
        }

        return null;
    }

}
