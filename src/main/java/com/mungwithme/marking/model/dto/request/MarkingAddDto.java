package com.mungwithme.marking.model.dto.request;


import com.mungwithme.marking.model.Visibility;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
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

