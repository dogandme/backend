package com.mungwithme.marking.model.dto.response;


import com.mungwithme.marking.model.entity.Marking;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TempMarkingInfoResponseDto extends MarkingInfoResponseDto {
    private Boolean isTempSaved;

    public void updateIsTempSaved(boolean isTempSaved) {
        this.isTempSaved = isTempSaved;
    }
}
