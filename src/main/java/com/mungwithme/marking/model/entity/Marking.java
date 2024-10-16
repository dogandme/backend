package com.mungwithme.marking.model.entity;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.common.base.BaseTimeEntity;
import com.mungwithme.common.util.TokenUtils;
import com.mungwithme.likes.model.entity.MarkingLikes;
import com.mungwithme.marking.model.enums.Visibility;
import com.mungwithme.marking.model.dto.request.MarkingAddDto;
import com.mungwithme.user.model.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 마킹 ENTITY
 */
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(indexes = {@Index(name = "idx_marking_id", columnList = "id", unique = true),
    @Index(name = "idx_marking_token", columnList = "token", unique = true)})
public class Marking extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;              // 작성

    @JoinColumn(name = "address_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Address address;          // 주소

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String region; // 지역

    @Column(nullable = false)
    private Double lat;        // 위도
    @Column(nullable = false)
    private Double lng;       // 경도

    @Column(length = 150)
    private String content;         // 내용 150 자 이내에

    @Column(nullable = false)
    private Boolean isTempSaved;    // 임시 저장 여부 true일 경우 임시저장, false 면 임시저장 X

    @Column(nullable = false)
    private Boolean isDeleted;      // 마킹 삭제 여부 true일 경우 삭제, false 면 삭제 X

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Visibility isVisible;   // 마킹 권한 보기 설정


    @OneToMany(mappedBy = "marking", cascade = CascadeType.ALL)
    private List<MarkImage> images = new ArrayList<>();   // One(marking)-to-Many(images) Join

    @OneToMany(mappedBy = "marking", cascade = CascadeType.ALL)
    private Set<MarkingSaves> saves = new HashSet<>();   // One(marking)-to-Many(images) Join

    @OneToMany(mappedBy = "marking", cascade = CascadeType.ALL)
    private Set<MarkingLikes> likes = new HashSet<>();   // One(marking)-to-Many(images) Join


    @Builder
    public Marking(Long id, User user, Address address, String token, String region, Double lat, Double lng,
        String content,
        Boolean isTempSaved, Boolean isDeleted, Visibility isVisible) {
        this.id = id;
        this.user = user;
        this.address = address;
        this.token = token;
        this.region = region;
        this.lat = lat;
        this.lng = lng;
        this.content = content;
        this.isTempSaved = isTempSaved;
        this.isDeleted = isDeleted;
        this.isVisible = isVisible;
    }

    public static Marking create(MarkingAddDto markingAddDto, User user,Address address) {
        return Marking.builder().content(markingAddDto.getContent())
            .region(markingAddDto.getRegion())
            .lat(markingAddDto.getLat())
            .lng(markingAddDto.getLng())
            .isDeleted(false)
            .address(address)
            .token(TokenUtils.getToken())
            .isVisible(markingAddDto.getIsVisible())
            .user(user).build();
    }

    public void updateIsTempSaved(boolean isTempSaved) {
        this.isTempSaved = isTempSaved;
    }

    public void updateIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }


    public void updateContent(String content) {
        this.content = content;
    }

    public void updateIsVisible(Visibility isVisible) {
        this.isVisible = isVisible;
    }

}
