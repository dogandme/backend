package com.mungwithme.marking.model.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * 마킹 이미지 ENTITY
 *
 */
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class MarkImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "marking_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Marking marking;   // 마킹아이디

    @Column(nullable = false)
    private String imageUrl;   // imageUrl

    @CreationTimestamp
    private Date regDt;        // 등록일



}
