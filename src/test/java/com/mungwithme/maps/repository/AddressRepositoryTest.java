package com.mungwithme.maps.repository;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.address.repository.AddressRepository;
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

        List<Address> allWithinDistance = addressRepository.findAllWithinDistance(lng, lat, radius1);

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
}