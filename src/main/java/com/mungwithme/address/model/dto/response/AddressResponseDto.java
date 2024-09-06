package com.mungwithme.address.model.dto.response;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddressResponseDto {

    private Long id;

    private String province; // 시 혹은 도

    private String cityCounty; // 시,군,구

    private String district; // 구

    private String subDistrict; // 읍면동





}
