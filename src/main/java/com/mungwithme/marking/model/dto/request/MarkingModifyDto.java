package com.mungwithme.marking.model.dto.request;


import com.mungwithme.marking.model.Visibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MarkingModifyDto {


    @NotNull
    @Min(value = 1)
    private Long id; // 마킹 아이디

    @NotBlank
    @Size(max = 150)
    private String content;

    @NotNull
    private Visibility isVisible;

    @NotNull
    private Set<Long> removeIds;

    // 임시 저장 여부
    // true 일 경우 임시 저장
    // false 일 경우 임시 저장 X
    @NotNull
    private Boolean isTempSaved;


}

