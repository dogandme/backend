package com.mungwithme.maps.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationBoundsDTO {




    private Double southBottomLat;
    private Double northTopLat;
    private Double southLeftLng;
    private Double northRightLng;

}
