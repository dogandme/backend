package com.mungwithme.pet.model.entity;

import com.mungwithme.common.base.BaseTimeEntity;
import com.mungwithme.common.converter.StringListConverter;
import com.mungwithme.user.model.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;


@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(uniqueConstraints ={@UniqueConstraint(columnNames = {"user_id"})} )
public class Pet extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id")                // 컬럼명 : pet_id
    private Long id;

    private String name;                    // 애완동물 이름
    private String description;             // 간단소개
    private String profile;                 // 프로필 이미지
    private String breed;                   // 강아지 종

    @Convert(converter = StringListConverter.class)
    private List<String> personalities;     // 성격

    @OneToOne(fetch = FetchType.LAZY)       // One(User)-to-One(Pet) Join
    @JoinColumn(name = "user_id")           // 외래 키 설정
    private User user;
}
