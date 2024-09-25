package com.mungwithme.maps.dto.response;


import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationBoundsDTO {

    @NotNull(message = "{error.NotNull}")
    private Double southBottomLat;
    @NotNull(message = "{error.NotNull}")
    private Double northTopLat;
    @NotNull(message = "{error.NotNull}")
    private Double southLeftLng;
    @NotNull(message = "{error.NotNull}")
    private Double northRightLng;

}
