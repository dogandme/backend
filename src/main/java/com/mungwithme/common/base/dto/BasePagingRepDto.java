package com.mungwithme.common.base.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@AllArgsConstructor
public class BasePagingRepDto {
    private Long totalElements;
    private int totalPages;
    private Pageable pageable;

}
