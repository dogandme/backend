package com.mungwithme.maps.dto.response;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PRIVATE)
public class Location {
    private double lat;
    private double lng;

    // 기본 생성자
    public Location() {}
    @Override
    public String toString() {
        return "Location{" +
            "lat=" + lat +
            ", lng=" + lng +
            '}';
    }
}
