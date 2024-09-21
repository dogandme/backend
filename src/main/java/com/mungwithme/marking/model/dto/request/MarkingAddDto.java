package com.mungwithme.marking.model.dto.request;


import com.mungwithme.marking.model.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MarkingAddDto {

    @NotBlank
    private String region;

    @Size(max = 150)
    @NotBlank
    private String content;

    @NotNull
    private Double lat;

    @NotNull
    private Double lng;

    @NotNull
    private Visibility isVisible;

}

