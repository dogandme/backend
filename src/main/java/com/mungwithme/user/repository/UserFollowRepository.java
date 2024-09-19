package com.mungwithme.user.repository;

import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.model.entity.UserFollows;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserFollowRepository extends JpaRepository<UserFollows,Long> {



    @Query("select f from UserFollows f where f.followingUser = :followingUser and f.followerUser = :followerUser ")
    Optional<UserFollows> findByFollowingUser(@Param("followingUser") User followingUser,@Param("followerUser") User followerUser);

}
