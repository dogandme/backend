package com.mungwithme.marking.model.dto.request;


import com.mungwithme.marking.model.Visibility;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MarkingAddDto {

    private String title;

    private Double lat;

    private Double lng;

    private String content;

    private Visibility visibility;
}
