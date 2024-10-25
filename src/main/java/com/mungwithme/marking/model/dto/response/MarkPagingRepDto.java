package com.mungwithme.marking.model.dto.response;


import com.mungwithme.common.base.dto.BasePagingRepDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
public class MarkPagingRepDto extends BasePagingRepDto {


    private List<MarkRepDto> marks;

    public MarkPagingRepDto(List<MarkRepDto> marks, long totalElements, int totalPages, Pageable pageable) {
        super(totalElements, totalPages, pageable);
        this.marks = marks;
    }
}
