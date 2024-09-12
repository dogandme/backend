package com.mungwithme.marking.model.dto.request;


import com.mungwithme.marking.model.Visibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MarkingRemoveDto {

    /**
     * ex)
     */
    @NotNull
    @Min(value = 1)
    private Long id; // 마킹 아이디


}

