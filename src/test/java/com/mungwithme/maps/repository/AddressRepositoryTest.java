package com.mungwithme.maps.repository;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.address.repository.AddressRepository;
import com.mungwithme.maps.dto.response.LocationBoundsDto;
import com.mungwithme.maps.dto.response.LocationBoundsDto;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.service.marking.MarkingSearchService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;


@SpringBootTest
class AddressRepositoryTest {


    @Autowired
    AddressRepository addressRepository;

    @Autowired
    MarkingSearchService markingSearchService;
    @Test
    void findNearbyMarkers() {

        //현재 위도 좌표 (y 좌표)
        double northTopLat = 35.545047500080756;
        //현재 경도 좌표 (x 좌표)
        double northRightLng = 129.3521825968079;

        //현재 위도 좌표 (y 좌표)
        double southBottomLat = 35.520204401760736;
        //현재 경도 좌표 (x 좌표)
        double southLeftLng = 129.32615169340926;

        LocationBoundsDto locationBoundsDto = new LocationBoundsDto(southBottomLat, northTopLat, southLeftLng,
            northRightLng);

        try {
            List<MarkingInfoResponseDto> nearbyMarkers = markingSearchService.findNearbyMarkers(locationBoundsDto);
            System.out.println("nearbyMarkers.size() = " + nearbyMarkers.size());
        }catch (Exception e) {

            System.out.println("e.getMessage() = " + e.getMessage());
        }





    }
    /**
     * 일반 like 사용시
     * <p>
     * 데이터 6000개 기준
     * <p>
     * 492ms
     */
    @Test
    void findAllByDistrict() {
        String district = "%동%";

        Sort sort = Sort.by(
            Order.asc("id")
        );

        PageRequest pageRequest = PageRequest.of(0, 6000, sort);

        List<Address> content = addressRepository.findAllBySubDist(district, pageRequest);

//        for (Address address : content) {
//            System.out.println(" ===================================" );
//            System.out.println("address.getProvince() = " + address.getProvince());
//            System.out.println("address.getCityCounty() = " + address.getCityCounty());
//            System.out.println("address.getDistrict() = " + address.getDistrict());
//            System.out.println("address.getSubDistrict() = " + address.getSubDistrict());
//            System.out.println(" ===================================" );
//        }

    }

    /**
     * Fulltext search 및
     * BOOLEAN MODE 사용시
     * <p>
     * <p>
     * <p>
     * 데이터 6000개 기준
     * <p>
     * 396ms
     */
    @Test
    void findAllByDistrict1() {
        String district = "영등*";

        Sort sort = Sort.by(
            Order.desc("id")
        );

        PageRequest pageRequest = PageRequest.of(0, 7, sort);

        List<Address> content = addressRepository.findAllBySubDist(district, pageRequest);

//
        for (Address address : content) {
            System.out.println(" ===================================");
            System.out.println("address.getProvince() = " + address.getProvince());
            System.out.println("address.getCityCounty() = " + address.getCityCounty());
            System.out.println("address.getDistrict() = " + address.getDistrict());
            System.out.println("address.getSubDistrict() = " + address.getSubDistrict());
            System.out.println(" ===================================");
        }
    }


    /**
     *  위경도 이용한 동네 검색
     *
     */
    @Test
    public void findAllWithinDistance() {
        // given
        double lng = 126.7231;
        double lat = 36.37648;
        double radius = 5000; //  5km
        double radius1 = 10000; //  10km
        // when


        Sort sort = Sort.by(
            Order.desc("id")
        );
        PageRequest pageRequest = PageRequest.of(0, 7, sort);

        List<Address> allWithinDistance = addressRepository.findAllWithinDistance(lng, lat, radius1,null);

        // then

        for (Address address : allWithinDistance) {
            System.out.println(" ===================================");
            System.out.println("address.getProvince() = " + address.getProvince());
            System.out.println("address.getCityCounty() = " + address.getCityCounty());
            System.out.println("address.getDistrict() = " + address.getDistrict());
            System.out.println("address.getSubDistrict() = " + address.getSubDistrict());
            System.out.println(" ===================================");
        }

    }

    @Test
    void getDistance() {


//        var southWest = bounds.getSouthWest();  // 남서쪽 (left, bottom)
//        var northEast = bounds.getNorthEast();  // 북동쪽 (right, top)

        //현재 위도 좌표 (y 좌표)
        double northTopLat = 35.545047500080756;
        //현재 경도 좌표 (x 좌표)
        double northRightLng = 129.3521825968079;

        //현재 위도 좌표 (y 좌표)
        double southBottomLat = 35.520204401760736;
        //현재 경도 좌표 (x 좌표)
        double southLeftLng = 129.32615169340926;

        List<Address> addressInBounds = addressRepository.findAddressInBounds(southBottomLat, northTopLat, southLeftLng, northRightLng);
//        List<Address> addressInBounds = addressRepository.findAddressInBounds(topLat, bottomLat, rightLng, leftLng);

        for (Address addressInBound : addressInBounds) {

            System.out.println(" ============================================ ");
            System.out.println("addressInBound.getId() = " + addressInBound.getId());
            System.out.println("addressInBound.getCityCounty() = " + addressInBound.getCityCounty());
            System.out.println("addressInBound.getDistrict() = " + addressInBound.getDistrict());
            System.out.println("addressInBound.getSubDistrict() = " + addressInBound.getSubDistrict());
            System.out.println("addressInBound.getLat() = " + addressInBound.getLat());
            System.out.println("addressInBound.getLng() = " + addressInBound.getLng());
            System.out.println(" ============================================ ");

        }
    }



}