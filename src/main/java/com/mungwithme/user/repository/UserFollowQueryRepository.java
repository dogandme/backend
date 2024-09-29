package com.mungwithme.user.repository;

import com.mungwithme.user.model.entity.UserFollows;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserFollowQueryRepository extends JpaRepository<UserFollows, Long> {

    @Query("SELECT distinct uf.followerUser.id FROM UserFollows uf "
            + " WHERE uf.followingUser.id = :userId ")
    List<Long> findIdsByFollowingUserId(@Param("userId") long userId);

    @Query("SELECT distinct uf.followingUser.id FROM UserFollows uf "
            + " WHERE uf.followerUser.id = :userId ")
    List<Long> findIdsByFollowerUserId(Long userId);
}
