package com.mungwithme.common.jdbc.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface JdbcRepository<T> {

    void saveAll(List<T> entityList, LocalDateTime createdDateTime);

    void save(T entity);

    void delete(T entity);
    void deleteAll(T entity);
}
