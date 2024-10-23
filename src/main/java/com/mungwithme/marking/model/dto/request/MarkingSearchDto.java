package com.mungwithme.marking.model.dto.request;


import com.mungwithme.maps.dto.response.LocationBoundsDto;
import com.mungwithme.marking.model.enums.SortType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarkingSearchDto {

    private Double lat;
    private Double lng;

    public MarkingSearchDto(){
        this.lat = 0.0;
        this.lng = 0.0;
    }

}
