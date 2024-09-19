package com.mungwithme.marking.repository.markingSaves;

import com.mungwithme.likes.model.entity.Likes;
import com.mungwithme.likes.model.enums.ContentType;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.model.entity.MarkingSaves;
import com.mungwithme.user.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface MarkingSavesRepository extends JpaRepository<MarkingSaves,Long> {


    @Query("select s from MarkingSaves s where s.user =:user and s.marking.id =:markingId ")
    Optional<MarkingSaves> fetchSaves(@Param("user") User user, @Param("markingId") long markingId);


    @Modifying(clearAutomatically = true)
    @Query("delete from MarkingSaves s where s.marking =:marking ")
    void deleteAllByMarking(@Param("marking") Marking marking);



}
