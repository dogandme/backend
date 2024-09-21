package com.mungwithme.marking.model.dto.response;


import com.mungwithme.marking.model.entity.MarkImage;
import java.util.Date;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarkImageResponseDto {

    private Long id;
    private String imageUrl;
    private Integer lank;
    private Date regDt;

    public MarkImageResponseDto(MarkImage markImage) {
        this.id = markImage.getId();
        this.imageUrl = markImage.getImageUrl();
        this.lank = markImage.getLank();
        this.regDt = markImage.getRegDt();
    }
}
