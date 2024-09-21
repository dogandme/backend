package com.mungwithme.maps.dto.response;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
public class GeocodingResponseDto {

    private String region;

    public GeocodingResponseDto(String region) {
        this.region = region;
    }
}
