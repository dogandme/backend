package com.mungwithme.user.model.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"follower_uid", "following_uid"})})
@Getter
@Setter(PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Entity
public class UserFollows {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 팔로우를 하는 사람들
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "follower_uid", nullable = false)
    private User followerUser;

    // 팔로우를 당하는 유저
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "following_uid", nullable = false)
    private User followingUser;

    @CreationTimestamp
    private Date regDt;             // 등록일

    @UpdateTimestamp
    private Date modDt;             // 수정일

    public static UserFollows create(User followerUser, User followingUser) {
        UserFollows userFollows = new UserFollows();
        userFollows.setFollowerUser(followerUser);
        userFollows.setFollowingUser(followingUser);
        return userFollows;
    }

}
