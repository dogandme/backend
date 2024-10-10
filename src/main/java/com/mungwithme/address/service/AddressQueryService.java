package com.mungwithme.address.service;


import com.mungwithme.address.model.dto.request.AddressCoordinatesDto;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.address.model.dto.request.AddressSearchDto;
import com.mungwithme.address.model.dto.response.AddressResponseDto;
import com.mungwithme.address.model.entity.Address;
import com.mungwithme.address.repository.AddressRepository;
import com.mungwithme.common.util.GeoUtils;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
public class AddressQueryService {

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
    public List<AddressResponseDto> findListBySubDist(AddressSearchDto addressSearchDto, int pageNumber,
        int pageSize) {
        String keyword = addressSearchDto.getKeyword();
        if (!StringUtils.hasText(keyword)) {
            throw new IllegalArgumentException("error.arg.keyword");
        }
        String district = addressSearchDto.getKeyword() + "*";

        Sort sort = Sort.by(
            Order.desc("id")
        );
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        List<Address> addressList = addressRepository.findAllBySubDist(district, pageRequest);

        if (addressList.isEmpty()) {
            throw new ResourceNotFoundException("error.notfound.keyword");
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
    public List<AddressResponseDto> findListByLngLat(AddressCoordinatesDto coordinatesDto, int pageNumber,
        int pageSize, int radius) {

        double lat = coordinatesDto.getLat();
        double lng = coordinatesDto.getLng();

        // 좌표 값 확인

        List<Address> addressList = findAllWithinDistance(radius, lat, lng, pageNumber, pageSize);

        if (addressList.isEmpty()) {
            throw new ResourceNotFoundException("error.notfound.coordinates");
        }

        return addressList.stream().map(address ->
            AddressResponseDto.builder().id(address.getId()).cityCounty(address.getCityCounty())
                .province(address.getProvince())
                .district(address.getDistrict()).subDistrict(address.getSubDistrict()).build()
        ).toList();
    }

    public List<Address> findAllWithinDistance(int radius, double lat, double lng, int pageNumber, int pageSize) {

        GeoUtils.isWithinKorea(lat, lng);

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        return addressRepository.findAllWithinDistance(lng, lat, radius, pageRequest);
    }

    public Optional<Address> findById(long id) {
        return addressRepository.findById(id);
    }

    public List<Address> findByIds(Set<Long> ids) {
        return addressRepository.findAllById(ids);
    }


}
