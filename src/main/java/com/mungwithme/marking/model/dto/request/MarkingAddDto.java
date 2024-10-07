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

    @NotBlank(message = "{error.NotBlank}")
    private String region;

    @Size(max = 150,message = "{error.size.content}")
    @NotNull(message = "{error.NotBlank}")
    private String content;

    @NotNull(message = "{error.NotNull}")
    private Double lat;

    @NotNull(message = "{error.NotNull}")
    private Double lng;

    @NotNull(message = "{error.NotNull}")
    private Visibility isVisible;

}

