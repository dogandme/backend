package com.mungwithme.marking.repository.impl;


import com.mungwithme.common.jdbc.repository.JdbcRepository;
import com.mungwithme.marking.model.entity.MarkImage;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.repository.markImge.MarkImageRepository;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

// 배치 처리를 위한 JdbcTemplate 사용
@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarkImageRepositoryImpl implements JdbcRepository<MarkImage> {


    private final JdbcTemplate jdbcTemplate;




    @Override
    public void saveAll(List<MarkImage> entityList, LocalDateTime createdDateTime) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO mark_image "
                + "(marking_id,lank,image_Url,reg_Dt) "
                + "VALUES (?,?,?,?)",
            entityList, 50,
            (PreparedStatement ps, MarkImage markImage) -> {
                ps.setLong(1, markImage.getMarking().getId());
                ps.setInt(2, markImage.getLank());
                ps.setString(3, markImage.getImageUrl());
                ps.setTimestamp(4, Timestamp.valueOf(createdDateTime));
            });
    }

    @Override
    public void save(MarkImage entity) {

    }

    @Override
    public void delete(MarkImage entity) {

    }

    @Override
    public void deleteAll(MarkImage entity) {

    }




}
