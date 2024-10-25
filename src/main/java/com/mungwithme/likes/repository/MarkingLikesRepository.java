package com.mungwithme.likes.repository;

import com.mungwithme.likes.model.dto.response.LikeCountResponseDto;
import com.mungwithme.likes.model.entity.MarkingLikes;
import com.mungwithme.user.model.entity.User;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MarkingLikesRepository extends JpaRepository<MarkingLikes, Long> {


    @Query("select l from MarkingLikes l where l.user =:user and l.marking.id =:markingId")
    Optional<MarkingLikes> fetchLikes(@Param("user") User user,
        @Param("markingId") long markingId);


    @Modifying(clearAutomatically = true)
    @Query("delete from MarkingLikes l where l.marking.id =:markingId ")
    void deleteAllByMarkingId(
        @Param("markingId") long markingId);


    @Modifying(clearAutomatically = true)
    @Query("delete from MarkingLikes l where l.marking.id in (:markingIds) ")
    void deleteAllByContentIds(
        @Param("markingIds") Set<Long> markingIds);

    @Modifying(clearAutomatically = true)
    @Query("delete from MarkingLikes l where l.user = :user ")
    void deleteAllByUser(@Param("user") User user);



    //    COALESCE(count(s.id),0)
    @Query("select new com.mungwithme.likes.model.dto.response.LikeCountResponseDto(l.marking.id,"
        + "COALESCE(count(l.marking.id),0)) from MarkingLikes l "
        + "where l.marking.id in (:markingIds) group by l.marking.id ")
    Set<LikeCountResponseDto> fetchLikeCounts(@Param("markingIds")Set<Long> markingIds);

}
