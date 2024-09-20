package com.mungwithme.marking.model.dto.response;


import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyMarkingsResponseDto {



    private Boolean isMyProfile;
    private Long tempCount;
    private List<MarkingInfoResponseDto> markings;


}
