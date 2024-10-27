package com.mungwithme.likes.model.dto.request;


import com.mungwithme.likes.model.enums.ContentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikesAddDto {

    @NotNull(message = "{error.NotNull}")
    @Min(value = 1)
    private Long contentId;
    @NotNull(message = "{error.NotNull}")
    private ContentType contentType;
}