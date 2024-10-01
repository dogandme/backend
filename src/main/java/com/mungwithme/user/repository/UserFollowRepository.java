package com.mungwithme.user.repository;

import com.mungwithme.likes.model.enums.ContentType;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.model.entity.UserFollows;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserFollowRepository extends JpaRepository<UserFollows, Long> {




    @Modifying(clearAutomatically = true)
    @Query("delete from UserFollows f where f.followingUser =:user or f.followerUser =:user ")
    void deleteAllByUser(@Param("user") User user);
}
