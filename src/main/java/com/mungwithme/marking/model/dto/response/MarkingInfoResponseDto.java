package com.mungwithme.marking.model.dto.response;


import com.mungwithme.marking.model.Visibility;
import com.mungwithme.marking.model.entity.Marking;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarkingInfoResponseDto {

    private Long id;

    private String title;
    private String content;

    private Double lat;
    private Double lng;

    private Visibility isVisible;
    private Date regDt;

    private Long userId;
    private String nickName;

    // 자신의 포스트 인지 확인
    private Boolean isOwner;

    private List<MarkImageResponseDto> images;

    public MarkingInfoResponseDto(Marking marking) {
        this.id = marking.getId();

        this.userId = marking.getUser().getId();
        this.nickName = marking.getUser().getNickname();

        this.title = marking.getTitle();
        this.content = marking.getContent();
        this.lat = marking.getLat();
        this.lng = marking.getLng();
        this.isVisible = marking.getIsVisible();
        this.regDt = marking.getRegDt();
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
