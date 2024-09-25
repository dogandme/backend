package com.mungwithme.address.model.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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


    @NotNull(message = "{error.NotNull}")
    private Double lat;

    @NotNull(message = "{error.NotNull}")
    private Double lng;


}
