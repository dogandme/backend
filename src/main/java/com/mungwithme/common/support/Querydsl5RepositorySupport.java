package com.mungwithme.common.support;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public abstract class Querydsl5RepositorySupport {

    private EntityManager entityManager;
    private JPAQueryFactory queryFactory;

    public Querydsl5RepositorySupport(Class<?> domainClass) {
        Assert.notNull(domainClass, "Domain class must not be null!");
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {

        Assert.notNull(entityManager, "EntityManager must not be null!");
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @PostConstruct
    public void validate() {
        Assert.notNull(entityManager, "EntityManager must not be null!");
        Assert.notNull(queryFactory, "QueryFactory must not be null!");
    }

    protected JPAQueryFactory getQueryFactory() {
        return queryFactory;
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected <T> JPAQuery<T> select(Expression<T> expr) {
        return getQueryFactory().select(expr);
    }

    protected <T> JPAQuery<T> selectFrom(EntityPath<T> from) {
        return getQueryFactory().selectFrom(from);
    }

    /**
     * 페이징 처리 - 하나의 쿼리로 콘텐츠와 카운트를 동시에 적용.
     */
    protected <T> Page<T> applyPagination(Pageable pageable, Function<JPAQueryFactory, JPAQuery<T>> contentQuery) {
        JPAQuery<T> jpaQuery = contentQuery.apply(getQueryFactory());
        List<T> content = jpaQuery.fetch();
        return PageableExecutionUtils.getPage(content, pageable, jpaQuery::fetchCount);
    }

    /**
     * 페이징 처리 - 콘텐츠 쿼리와 카운트 쿼리를 분리하여 사용.
     */
    protected <T> Page<T> applyPagination(Pageable pageable,
        Function<JPAQueryFactory, JPAQuery<T>> contentQuery,
        Function<JPAQueryFactory, JPAQuery<Long>> countQuery) {
        JPAQuery<T> jpaContentQuery = contentQuery.apply(getQueryFactory());
        List<T> content = jpaContentQuery.fetch();

        JPAQuery<Long> countResult = countQuery.apply(getQueryFactory());
        Long total = countResult.fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> total != null ? total : 0L);
    }

    /**
     * 페이징 처리 - 콘텐츠 쿼리와 카운트 쿼리를 분리하여 사용.
     */
    protected <T> Page<T> applyPagination(Pageable pageable,
        JPAQuery<T> contentQuery,
        JPAQuery<Long> countQuery) {
        List<T> content = contentQuery.fetch();
        Long total = countQuery.fetchOne();
        return PageableExecutionUtils.getPage(content, pageable, () -> total != null ? total : 0L);
    }
}