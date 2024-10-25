package com.mungwithme.marking.model.dto.response;


import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarkRepDto {


    private Long markingId;

    private String previewImage;

    private Double lat;

    private Double lng;

    @QueryProjection
    public MarkRepDto(Long markingId, String previewImage, Double lat, Double lng) {
        this.markingId = markingId;
        this.previewImage = previewImage;
        this.lat = lat;
        this.lng = lng;
    }
}
