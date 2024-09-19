package com.mungwithme.marking.model.entity;


import com.mungwithme.marking.model.dto.request.MarkingAddDto;
import com.mungwithme.user.model.entity.User;
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
 * 마킹 즐겨찾기 ENTITY
 */
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class MarkingSaves {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saves_id")
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;              // 작성자

    @JoinColumn(name = "marking_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Marking marking;              // 마킹

    @CreationTimestamp
    private Date regDt;             // 등록일

    public static MarkingSaves create(User user,Marking marking) {
        return MarkingSaves.builder().user(user).marking(marking).build();
    }

}
