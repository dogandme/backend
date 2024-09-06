package com.mungwithme.address.service;

import static org.junit.jupiter.api.Assertions.*;

import com.mungwithme.address.model.dto.request.AddressSearchDto;
import com.mungwithme.address.model.dto.response.AddressResponseDto;
import com.mungwithme.address.model.entity.Address;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class AddressSearchServiceTest {


    @Autowired
    AddressSearchService addressSearchService;
    @Test
    void fetchListBySubDist() {

        AddressSearchDto searchDto = new AddressSearchDto("영등");
        List<AddressResponseDto> addressResponseDtoList = addressSearchService.fetchListBySubDist(searchDto, 0, 7);

        for (AddressResponseDto addressResponseDto : addressResponseDtoList) {

            System.out.println(" ==================================== ");
            System.out.println("addressResponseDto.getId() = " + addressResponseDto.getId());
            System.out.println("addressResponseDto.getProvince() = " + addressResponseDto.getProvince());
            System.out.println("addressResponseDto.getCityCounty() = " + addressResponseDto.getCityCounty());
            System.out.println("addressResponseDto.getDistrict() = " + addressResponseDto.getDistrict());
            System.out.println("addressResponseDto.getSubDistrict() = " + addressResponseDto.getSubDistrict());
            System.out.println(" ==================================== ");
        }
    }
}