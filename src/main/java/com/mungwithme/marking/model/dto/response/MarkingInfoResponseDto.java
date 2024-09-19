package com.mungwithme.marking.model.dto.response;


import com.mungwithme.marking.model.enums.Visibility;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.pet.model.dto.response.PetInfoResponseDto;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarkingInfoResponseDto {
    private Long id;
    private String region;
    private String content;
    private Visibility isVisible;
    private Date regDt;

    private Long userId;
    private String nickName;

    // 자신의 포스트 인지 확인
    private Boolean isOwner;

    private Boolean isTempSaved;

    private Double lat;
    private Double lng;



    private PetInfoResponseDto pet;
    private List<MarkImageResponseDto> images;

    public MarkingInfoResponseDto(Marking marking) {
        this.id = marking.getId();

        this.userId = marking.getUser().getId();
        this.nickName = marking.getUser().getNickname();

        this.region = marking.getRegion();
        this.content = marking.getContent();
        this.lat = marking.getLat();
        this.lng = marking.getLng();
        this.isVisible = marking.getIsVisible();
        this.regDt = marking.getRegDt();
        this.isTempSaved = marking.getIsTempSaved();

        List<MarkImageResponseDto> imageDtos = new java.util.ArrayList<>(
            marking.getImages().stream().map(MarkImageResponseDto::new)
                .toList());
        imageDtos.sort(Comparator.comparing(MarkImageResponseDto::getLank));
        this.images = imageDtos;
    }

    public void updateIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

}
