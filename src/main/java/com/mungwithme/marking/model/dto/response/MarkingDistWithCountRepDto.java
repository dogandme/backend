package com.mungwithme.marking.model.dto.response;


import com.mungwithme.address.model.dto.response.AddressResponseDto;
import com.mungwithme.address.model.entity.Address;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter(value = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarkingDistWithCountRepDto extends AddressResponseDto {
    private Double lat;
    private Double lng;
    private Long markTotalCount;

    public MarkingDistWithCountRepDto(Address address, Long markTotalCount) {
        super(address.getId(), address.getProvince(), address.getCityCounty(), address.getDistrict(), address.getSubDistrict());
        this.lat = address.getLat();
        this.lng = address.getLng();
        this.markTotalCount = markTotalCount;
    }
}
