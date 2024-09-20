package com.mungwithme.likes.model.dto.response;

import com.mungwithme.likes.model.enums.ContentType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeCountResponseDto {

    private Long contentId;
    private ContentType contentType;
    private Long count;

    public LikeCountResponseDto(long contentId, ContentType contentType, long count) {
        this.contentId = contentId;
        this.contentType = contentType;
        this.count = count;
    }
}
