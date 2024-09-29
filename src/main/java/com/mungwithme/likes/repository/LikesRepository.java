package com.mungwithme.likes.repository;

import com.mungwithme.likes.model.dto.response.LikeCountResponseDto;
import com.mungwithme.likes.model.entity.Likes;
import com.mungwithme.likes.model.enums.ContentType;
import com.mungwithme.user.model.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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


    @Modifying(clearAutomatically = true)
    @Query("delete from Likes l where l.contentId in (:contentsIds) and l.contentType =:contentType ")
    void deleteAllByContentIds(
        @Param("contentsIds") Set<Long> contentsIds, @Param("contentType") ContentType contentType);


//    COALESCE(count(s.id),0)
    @Query("select new com.mungwithme.likes.model.dto.response.LikeCountResponseDto(l.contentId,l.contentType"
        + ",COALESCE(count(l.contentId),0)) from Likes l "
        + "where l.contentId in (:contentsIds) and l.contentType = :contentType group by l.contentId ")
    Set<LikeCountResponseDto> fetchLikeCounts(@Param("contentsIds")Set<Long> contentsIds,@Param("contentType") ContentType contentType);

}
