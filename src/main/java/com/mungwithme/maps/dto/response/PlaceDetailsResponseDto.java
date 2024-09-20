package com.mungwithme.maps.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceDetailsResponseDto {
    private String formattedAddress;
    private String name;
    private double latitude;
    private double longitude;

    public PlaceDetailsResponseDto(String formattedAddress, String name, double latitude, double longitude) {
        this.formattedAddress = formattedAddress;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getter and Setter methods
}
