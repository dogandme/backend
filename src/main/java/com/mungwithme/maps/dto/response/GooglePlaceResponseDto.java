package com.mungwithme.maps.dto.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PRIVATE)
public class GooglePlaceResponseDto {
    private List<PlaceResult> results;



}
