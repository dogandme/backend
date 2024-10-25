package com.mungwithme.user.model.dto.response;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoRepPagingDto {
    private List<UserInfoResponseDto> userInfos;
    private Long totalElements;
    private int totalPages;
    private Pageable pageAble;

}
