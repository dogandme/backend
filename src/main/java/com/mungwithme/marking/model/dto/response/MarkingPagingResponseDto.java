package com.mungwithme.marking.model.dto.response;

import java.util.List;
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

    public MarkingPagingResponseDto(List<MarkingInfoResponseDto> markings, Long totalElements, int totalPages,
        Pageable pageable) {
        this.markings = markings;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.pageable = pageable;
    }
}
