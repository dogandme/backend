package com.mungwithme.marking.model.dto.response;


import static lombok.AccessLevel.PRIVATE;

import com.mungwithme.marking.model.entity.Marking;
import java.util.Date;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(value = PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarkingInfoWithLikedResponseDto extends MarkingInfoResponseDto{


    private Long likedId;
    private Date likedRegDt;
    public MarkingInfoWithLikedResponseDto(Marking marking, long likedId,Date likedRegDt) {
        super(marking);
        this.likedId = likedId;
        this.likedRegDt = likedRegDt;
    }
}
