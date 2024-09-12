package com.mungwithme.address.service;


import com.mungwithme.address.model.dto.request.AddressCoordinatesDto;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.address.model.dto.request.AddressSearchDto;
import com.mungwithme.address.model.dto.response.AddressResponseDto;
import com.mungwithme.address.model.entity.Address;
import com.mungwithme.address.repository.AddressRepository;
import com.mungwithme.common.util.GeoUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 동네 검색 및 현재 위치 동네 정보를 제공하는 service
 * <p>
 * <p>
 * <p>
 * 만든이 : 임하늘 (lim642666@gmail.com)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressSearchService {

    private final AddressRepository addressRepository;


    /**
     * 회원 가입 시 키워드 동네 검색 API
     *
     * @param addressSearchDto
     *     읍면동 검색 키워드를 담은 dto
     * @param pageNumber
     *     page 시작 위치
     * @param pageSize
     *     데이터 size
     * @return
     */
    public List<AddressResponseDto> fetchListBySubDist(AddressSearchDto addressSearchDto, int pageNumber,
        int pageSize) {
        String keyword = addressSearchDto.getKeyword();
        if (!StringUtils.hasText(keyword)) {
            throw new IllegalArgumentException("keyword 는 필수입니다.");
        }
        String district = addressSearchDto.getKeyword() + "*";

        Sort sort = Sort.by(
            Order.desc("id")
        );
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        List<Address> addressList = addressRepository.findAllBySubDist(district, pageRequest);

        if (addressList.isEmpty()) {
            throw new ResourceNotFoundException("주소가 없습니다.");
        }
        return addressList.stream().map(address ->
            AddressResponseDto.builder().id(address.getId()).cityCounty(address.getCityCounty())
                .province(address.getProvince())
                .district(address.getDistrict()).subDistrict(address.getSubDistrict()).build()
        ).toList();
    }

    /**
     *
     */

    /**
     * 좌표를 사용하여 근처 미터 단위로 읍면동 리스트 검색 API
     *
     * @param coordinatesDto
     *     위경도를 담은 DTO
     * @param pageNumber
     *     page 시작 위치
     * @param pageSize
     *     데이터 size
     * @param radius
     *     m (1000m -> 1km)
     * @return
     */
    public List<AddressResponseDto> fetchListByLngLat(AddressCoordinatesDto coordinatesDto, int pageNumber,
        int pageSize, int radius) {

        double lat = coordinatesDto.getLat();
        double lng = coordinatesDto.getLng();

        // 좌표 값 확인
        if (!GeoUtils.isWithinKorea(lat,lng)) {
            throw new IllegalArgumentException("ex) 좌표 값이 정확하지 않습니다.");
        }

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        List<Address> addressList = addressRepository.findAllWithinDistance(lng, lat, radius, pageRequest);

        if (addressList.isEmpty()) {
            throw new ResourceNotFoundException("잘못된 위치 정보입니다.");
        }

        return addressList.stream().map(address ->
            AddressResponseDto.builder().id(address.getId()).cityCounty(address.getCityCounty())
                .province(address.getProvince())
                .district(address.getDistrict()).subDistrict(address.getSubDistrict()).build()
        ).toList();
    }




}
