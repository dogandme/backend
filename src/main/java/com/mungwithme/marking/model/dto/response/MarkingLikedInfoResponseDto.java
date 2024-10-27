package com.mungwithme.marking.model.dto.response;


import static lombok.AccessLevel.PRIVATE;

import java.time.LocalDateTime;
import java.util.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(value = PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MarkingLikedInfoResponseDto {


    private Long likedId;
    private LocalDateTime likedRegDt;
}