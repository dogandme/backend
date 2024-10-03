package com.mungwithme.marking.repository.markImge;

import com.mungwithme.marking.model.entity.MarkImage;
import com.mungwithme.marking.model.entity.Marking;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MarkImageRepository extends JpaRepository<MarkImage,Long> {





    @Modifying(clearAutomatically = true)
    @Query("delete from MarkImage i where i.marking in (:markings) ")
    void deleteAllByMarkings(@Param("markings") Set<Marking> markings);

}
