package com.mungwithme.marking.repository.markImge;

import com.mungwithme.marking.model.entity.MarkImage;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MarkImageQueryRepository extends JpaRepository<MarkImage,Long> {


    @Query("select i from MarkImage i where i.marking.id in (:markingIds) order by i.id asc ")
    List<MarkImage> findAllByMarkingIds(@Param("markingIds") Set<Long> markingIds);

}
