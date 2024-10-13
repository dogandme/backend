package com.mungwithme.marking.model.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.mungwithme.marking.model.entity.MarkImage;
import com.mungwithme.marking.model.entity.MarkingSaves;
import com.mungwithme.marking.model.enums.Visibility;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.pet.model.dto.response.PetInfoResponseDto;
import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.user.model.entity.User;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarkingInfoResponseDto {

    private Long markingId;
    private String region;
    private String content;
    private Visibility isVisible;

    private LocalDateTime regDt;

    private Long userId;

    private String nickName;

    // 자신의 포스트 인지 확인
    private Boolean isOwner;

    private Boolean isTempSaved;

    private Double lat;
    private Double lng;

    // 값이 널이면 json 객체에서 제외
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MarkingLikedInfoResponseDto likedInfo;
    // 값이 널이면 json 객체에서 제외
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MarkingSavedInfoResponseDto savedInfo;


    private MarkingCountDto countData;

    private PetInfoResponseDto pet;

    private List<MarkImageResponseDto> images;

    /**
     * 회원이 조회를 했을경우
     */
    public MarkingInfoResponseDto(Marking marking) {
        setData(marking);
        this.isOwner = marking.getUser().getId().equals(userId);
    }

    private void setData(Marking marking) {
        this.markingId = marking.getId();
        this.region = marking.getRegion();
        this.userId = marking.getUser().getId();

        this.nickName = marking.getUser().getNickname();
        this.content = marking.getContent();
        this.lat = marking.getLat();
        this.lng = marking.getLng();
        this.isTempSaved = marking.getIsTempSaved();
        this.isVisible = marking.getIsVisible();
        this.regDt = marking.getRegDt();
        this.countData = new MarkingCountDto();
        this.isOwner = false;
    }


    public void updateImage (List<MarkImage> markImages) {
        List<MarkImageResponseDto> imageDtos = new java.util.ArrayList<>(
            markImages.stream().map(MarkImageResponseDto::new)
                .toList());
        imageDtos.sort(Comparator.comparing(MarkImageResponseDto::getLank));
        this.images = imageDtos;

    }

    public void updateIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public void updateLikeCount(long count) {
        this.countData.updateLikedCount(count);
    }

    public void updateLikedInfo(long likeId, LocalDateTime regDt) {
        this.likedInfo = new MarkingLikedInfoResponseDto(likeId, regDt);
    }
    public void updateSavedInfo(long savedId, LocalDateTime regDt) {
        this.savedInfo = new MarkingSavedInfoResponseDto(savedId, regDt);
    }

    public void updatePet(Pet pet) {
        this.pet = PetInfoResponseDto.builder()
                .petId(pet.getId())
                .name(pet.getName())
                .profile(pet.getProfile())
                .build();
    }


}
