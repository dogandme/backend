package com.mungwithme.marking.model.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MarkingRemoveDto {

    /**
     * ex)
     */
    @NotNull(message = "{error.NotNull}")
    @Min(value = 1)
    private Long id; // 마킹 아이디


}

