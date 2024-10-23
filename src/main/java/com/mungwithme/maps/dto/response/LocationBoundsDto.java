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
public class LocationBoundsDto {


    private Double southBottomLat;
    private Double northTopLat;
    private Double southLeftLng;
    private Double northRightLng;

    public LocationBoundsDto(){
        this.southBottomLat = 0.0;
        this.northTopLat = 0.0;
        this.southLeftLng = 0.0;
        this.northRightLng = 0.0;
    }
}
