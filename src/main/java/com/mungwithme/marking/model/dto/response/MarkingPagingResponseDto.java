package com.mungwithme.marking.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
public class MarkingPagingResponseDto {




    private List<MarkingInfoResponseDto> markings;

    private Long totalElements;
    private int totalPages;
    private Pageable pageable;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isMyProfile;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long tempCount;


    @Builder
    public MarkingPagingResponseDto(List<MarkingInfoResponseDto> markings, Long totalElements, int totalPages,
        Pageable pageable, Boolean isMyProfile, Long tempCount) {
        this.markings = markings;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.pageable = pageable;
        this.isMyProfile = isMyProfile;
        this.tempCount = tempCount;
    }

    public MarkingPagingResponseDto(List<MarkingInfoResponseDto> markings, Long totalElements, int totalPages,
        Pageable pageable) {
        this.markings = markings;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.pageable = pageable;
    }
}
