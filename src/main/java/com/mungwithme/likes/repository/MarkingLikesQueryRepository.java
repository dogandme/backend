package com.mungwithme.likes.repository;

import com.mungwithme.likes.model.entity.MarkingLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import org.springframework.data.repository.query.Param;

public interface MarkingLikesQueryRepository extends JpaRepository<MarkingLikes, Long> {

    @Query("SELECT distinct l.marking.id FROM MarkingLikes l "
            + "where l.user.id = :userId")
    List<Long> findAllByUserIdAndContentType(@Param("userId") Long userId);



}
