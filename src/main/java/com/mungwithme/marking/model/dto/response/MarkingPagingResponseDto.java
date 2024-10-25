package com.mungwithme.marking.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mungwithme.common.base.dto.BasePagingRepDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
public class MarkingPagingResponseDto extends BasePagingRepDto {




    private List<MarkingInfoResponseDto> markings;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isMyProfile;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long tempCount;


    @Builder
    public MarkingPagingResponseDto(List<MarkingInfoResponseDto> markings, Long totalElements, int totalPages,
        Pageable pageable, Boolean isMyProfile, Long tempCount) {
        super(totalElements,totalPages,pageable);
        this.markings = markings;
        this.isMyProfile = isMyProfile;
        this.tempCount = tempCount;
    }

    public MarkingPagingResponseDto(List<MarkingInfoResponseDto> markings, Long totalElements, int totalPages,
        Pageable pageable) {
        super(totalElements,totalPages,pageable);
        this.markings = markings;

    }
}
