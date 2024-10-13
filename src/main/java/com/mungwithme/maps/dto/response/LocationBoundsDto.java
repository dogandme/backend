package com.mungwithme.maps.dto.response;


import com.mungwithme.marking.model.enums.SortType;
import com.mungwithme.user.model.enums.SocialType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationBoundsDto {


    @NotNull(message = "{error.NotNull}")
    private Double southBottomLat;
    @NotNull(message = "{error.NotNull}")
    private Double northTopLat;
    @NotNull(message = "{error.NotNull}")
    private Double southLeftLng;
    @NotNull(message = "{error.NotNull}")
    private Double northRightLng;

}
