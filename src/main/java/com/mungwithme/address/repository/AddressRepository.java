package com.mungwithme.address.repository;

import com.mungwithme.address.model.entity.Address;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface AddressRepository extends JpaRepository<Address, Long> {

    /**
     * 동네 검색 (읍,면,동)
     *
     * @param subDistrict
     * @param pageable
     * @return
     */

//    @Query(value = "SELECT * FROM Address a WHERE MATCH(a.subdistrict) AGAINST (?1 IN BOOLEAN MODE)", nativeQuery = true)
//    @Query(value = "select a from Address a where a.subDistrict like :subDistrict ")
//    Page<Address> findAllBySubDist(@Param("subDistrict") String subDistrict, Pageable pageable);


    //
    //    문자열을 단어 단위로 분리한 후 검색 규칙을 붙여서 검색을 할 수 있습니다. 행을 정렬하지 않습니다. 조건을 만족하면 반환하는 식입니다.
    //    예를 들어 이 쿼리에서는 MySQL 이 들어가고 YourSQL 이 없는 항목을 검색합니다.
    @Query(value = "SELECT * FROM Address WHERE MATCH(subdistrict) AGAINST (?1 IN BOOLEAN MODE)", nativeQuery = true)
    List<Address> findAllBySubDist(String subDistrict, Pageable pageable);


    /**
     *
         POINT()는 MySQL에서 공간 데이터 타입인 POINT를 생성하는 함수입니다.
         POINT() 함수는 지리적 좌표를 나타내는 POINT 객체를 생성합니다.

         POINT() 함수는 두 개의 인자를 가지며, 각각 x 좌표와 y 좌표입니다.
         일반적으로 경도(Longitude)와 위도(Latitude) 값을 사용하여 지리적 좌표를 표현합니다.

         예를 들어, POINT(127.1234, 37.5678)은 경도가 127.1234이고 위도가 37.5678인 지점을 나타내는 POINT 객체를 생성합니다.
         이를 통해 해당 레코드의 위치 정보를 POINT 객체로 저장하고 업데이트할 수 있습니다.
     *
     * @param lng
     * @param lat
     * @param radius
     * @return
     */
    @Query(value = "SELECT *, ST_Distance_Sphere(POINT(:lng, :lat), POINT(a.lng, a.lat)) as distance " +
        "FROM Address a " +
        "WHERE ST_Distance_Sphere(POINT(:lng, :lat), POINT(a.lng, a.lat)) <= :radius "
        + "ORDER BY distance ASC " ,
        nativeQuery = true)
    List<Address> findAllWithinDistance(
        @Param("lng") double lng,
        @Param("lat") double lat,
        @Param("radius") double radius,Pageable pageable );

    @Query("SELECT a FROM Address a WHERE a.lat BETWEEN :southBottomLat AND :northTopLat AND a.lng BETWEEN :southLeftLng AND :northRightLng")
    List<Address> findAddressInBounds(
        @Param("southBottomLat") double southBottomLat,
        @Param("northTopLat") double northTopLat,
        @Param("southLeftLng") double southLeftLng,
        @Param("northRightLng") double northRightLng
    );

}
