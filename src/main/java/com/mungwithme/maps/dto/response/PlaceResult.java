package com.mungwithme.maps.dto.response;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PRIVATE)
public class PlaceResult {

    private String name;
    private Geometry geometry;
    private String formattedAddress;  // 사람이 읽을 수 있는 주소
    private String placeId;
    private String vicinity;
    private String plusCode;          // 인코딩된 위치 참조 코드



}
