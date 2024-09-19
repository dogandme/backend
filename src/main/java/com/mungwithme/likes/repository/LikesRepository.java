package com.mungwithme.likes.repository;

import com.mungwithme.likes.model.entity.Likes;
import com.mungwithme.likes.model.enums.ContentType;
import com.mungwithme.user.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikesRepository extends JpaRepository<Likes, Long> {


    @Query("select l from Likes l where l.user =:user and l.contentType =:contentType and l.contentId =:contentId")
    Optional<Likes> fetchLikes(@Param("user") User user, @Param("contentType") ContentType contentType,
        @Param("contentId") long contentId);


    @Modifying(clearAutomatically = true)
    @Query("delete from Likes l where l.contentId =:contentId and l.contentType =:contentType ")
    void deleteAllByContentId(
        @Param("contentId") long contentId, @Param("contentType") ContentType contentType);
}
