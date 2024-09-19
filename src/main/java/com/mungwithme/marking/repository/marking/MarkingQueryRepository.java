package com.mungwithme.marking.repository.marking;

import com.mungwithme.marking.model.entity.Marking;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MarkingQueryRepository extends JpaRepository<Marking,Long> {



    @Query("select distinct m from Marking m join fetch m.user left join fetch m.images where m.id = :id and m.isDeleted = :isDeleted and m.isTempSaved =:isTempSaved")
    Optional<Marking> findById(@Param("id") long id, @Param("isDeleted") boolean isDeleted,@Param("isTempSaved") boolean isTempSaved);



}
