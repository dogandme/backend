package com.mungwithme.marking.repository.marking;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.marking.model.entity.Marking;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MarkingQueryRepository extends JpaRepository<Marking, Long> {


    @Query("select distinct m from Marking m left join fetch m.images where m.id = :id and m.isDeleted = :isDeleted and m.isTempSaved =:isTempSaved")
    Optional<Marking> findById(@Param("id") long id, @Param("isDeleted") boolean isDeleted,
        @Param("isTempSaved") boolean isTempSaved);


    @Query(
        "SELECT distinct m FROM Marking m left join fetch m.images WHERE m.lat "
            + " BETWEEN :southBottomLat AND :northTopLat AND m.lng BETWEEN :southLeftLng AND :northRightLng "
            + " and m.isTempSaved =:isTempSaved "
            + " and m.isDeleted =:isDeleted ")
    List<Marking> findMarkingInBounds(
        @Param("southBottomLat") double southBottomLat,
        @Param("northTopLat") double northTopLat,
        @Param("southLeftLng") double southLeftLng,
        @Param("northRightLng") double northRightLng,
        @Param("isDeleted") boolean isDeleted,
        @Param("isTempSaved") boolean isTempSaved
    );


}
