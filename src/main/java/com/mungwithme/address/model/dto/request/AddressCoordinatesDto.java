package com.mungwithme.address.model.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * 현재 위치의 좌표를 토대로
 * 동네검색 요청 DTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AddressCoordinatesDto {


    @NotBlank
    private Long lat;

    @NotBlank
    private Long lng;


}
