package com.mungwithme.maps.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GooglePlaceResponseDto {
    private List<PlaceResult> results;



}
