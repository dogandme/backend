package com.mungwithme.marking.model.dto.request;


import com.mungwithme.maps.dto.response.LocationBoundsDto;
import com.mungwithme.marking.model.enums.SortType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarkingSearchDto extends LocationBoundsDto {


    @NotNull(message = "{error.NotNull}")
    private Double lat;
    @NotNull(message = "{error.NotNull}")
    private Double lng;

}
