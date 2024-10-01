package com.mungwithme.likes.repository;

import com.mungwithme.likes.model.entity.Likes;
import com.mungwithme.likes.model.enums.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import org.springframework.data.repository.query.Param;

public interface LikesQueryRepository extends JpaRepository<Likes, Long> {

    @Query("SELECT distinct l.contentId FROM Likes l "
            + "where l.contentType = :contentType "
            + "and l.user.id = :userId")
    List<Long> findAllByUserIdAndContentType(@Param("userId") Long userId,
                                          @Param("contentType") ContentType contentType);



}
