package com.mungwithme.address.model.entity;


import static lombok.AccessLevel.PRIVATE;

import com.mungwithme.common.base.BaseTimeEntity;
import com.mungwithme.user.model.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter(value = PRIVATE)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity

// index 추가
@Table(name = "address",indexes = @Index(name = "idx_address_pk_id",columnList = "id"))
public class Address extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    //

    private String province; // 시 혹은 도 (특별시,광역시)

    @Column(name = "city_county")
    private String cityCounty; // 시,군,구

    private String district; // 구


    @Column(name = "subdistrict")
    private String subDistrict; // 읍면동



    private String village; // 리


    private Double lat; // 위도

    
    private Double lng; // 경도

}
