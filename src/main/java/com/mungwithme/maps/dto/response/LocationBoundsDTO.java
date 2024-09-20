package com.mungwithme.maps.dto.response;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationBoundsDTO {

    @NotNull
    private Double southBottomLat;
    @NotNull
    private Double northTopLat;
    @NotNull
    private Double southLeftLng;
    @NotNull
    private Double northRightLng;

}
