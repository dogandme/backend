package com.mungwithme.maps.dto.response;


import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class LocationBoundsDto {

    @NotNull
    private Double southBottomLat;
    @NotNull
    private Double northTopLat;
    @NotNull
    private Double southLeftLng;
    @NotNull
    private Double northRightLng;

}
