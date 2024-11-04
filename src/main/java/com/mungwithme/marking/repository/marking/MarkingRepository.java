package com.mungwithme.marking.repository.marking;


import com.mungwithme.marking.model.entity.Marking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MarkingRepository extends JpaRepository<Marking,Long> {


    @Modifying(clearAutomatically = true)
    @Query("update Marking m set m.isDeleted = :isDeleted  "
        + "where m =:marking ")
    void updateIsDeleted(@Param("isDeleted") boolean isDeleted,
        @Param("marking") Marking marking);


    
}
