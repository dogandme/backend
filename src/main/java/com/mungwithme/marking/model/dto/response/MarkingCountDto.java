package com.mungwithme.marking.model.dto.response;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarkingCountDto {

    private Long likedCount;

    private Long savedCount;


    public void updateLikedCount (long likedCount) {
        this.likedCount = likedCount;
    }

    public void updateSavedCount (long savedCount) {
        this.savedCount = savedCount;
    }

}
