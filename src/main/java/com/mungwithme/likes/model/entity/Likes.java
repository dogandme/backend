package com.mungwithme.likes.model.entity;


import com.mungwithme.common.base.BaseTimeEntity;
import com.mungwithme.likes.model.enums.ContentType;
import com.mungwithme.user.model.entity.User;
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
import java.util.Date;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;


@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Likes extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "likes_id")     // 컬럼명 : likes_id
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;              // 좋아요를 한 사용자

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;   // 좋아요한 컨텐츠 타입 ) 마킹,댓글 등등

    private Long contentId;         // 좋아요한 콘텐츠 아이디
    
    public static Likes create(User user,long contentId,ContentType contentType) {
        return Likes.builder().contentId(contentId).user(user).contentType(contentType).build();
    }


}
