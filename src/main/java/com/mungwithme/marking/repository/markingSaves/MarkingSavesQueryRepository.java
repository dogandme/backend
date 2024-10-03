package com.mungwithme.marking.repository.markingSaves;

import com.mungwithme.marking.model.entity.MarkingSaves;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import org.springframework.data.repository.query.Param;

public interface MarkingSavesQueryRepository extends JpaRepository<MarkingSaves, Long> {

    @Query("SELECT distinct s.marking.id FROM MarkingSaves s "
            + "where s.user.id = :userId")
    List<Long> findAllByUserId(@Param("userId") Long userId);

}
