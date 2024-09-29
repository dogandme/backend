package com.mungwithme.likes.repository;

import com.mungwithme.likes.model.entity.Likes;
import com.mungwithme.likes.model.enums.ContentType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LikesQueryRepository extends JpaRepository<Likes, Long> {

    @Query("SELECT distinct l.contentId FROM Likes l "
            + "where l.contentType = :contentType "
            + "and l.user.id = :userId")
    List<Long> findAllByUserIdAndContentType(@Param("contentType") Long userId,
                                          @Param("userId") ContentType contentType);
}
