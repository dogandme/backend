package com.mungwithme.marking.model.entity;

import com.mungwithme.marking.model.Visibility;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.Date;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * 마킹 ENTITY
 */
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Marking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;              // 작성자

    @Column(nullable = false)
    private String title;

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

    @CreationTimestamp
    private Date regDt;             // 등록일

    @UpdateTimestamp
    private Date modDt;             // 수정일

    @OneToMany(mappedBy = "marking", cascade = CascadeType.ALL)
    private Set<MarkImage> images;   // One(marking)-to-Many(images) Join


    public static Marking create(MarkingAddDto markingAddDto, User user) {
        return Marking.builder().content(markingAddDto.getContent())
            .title(markingAddDto.getTitle())
            .lat(markingAddDto.getLat())
            .lng(markingAddDto.getLng())
            .isDeleted(false)
            .isVisible(markingAddDto.getIsVisible())
            .user(user).build();
    }
    public void updateIsTempSaved (boolean isTempSaved) {
        this.isTempSaved = isTempSaved;
    }
    public void updateIsDeleted (boolean isDeleted) {
        this.isDeleted = isDeleted;
    }


    public void updateContent (String content) {
        this.content = content;
    }

    public void updateIsVisible (Visibility isVisible) {
        this.isVisible = isVisible;
    }

}