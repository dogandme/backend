package com.mungwithme.address.model.entity;


import static lombok.AccessLevel.PRIVATE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter(value = PRIVATE)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity

// index 추가
@Table(name = "address",indexes = @Index(name = "idx_address_pk_id",columnList = "id"))
public class Address {

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
