package com.mungwithme.user.repository;

import static com.mungwithme.user.model.entity.QUserNotify.userNotify;

import com.mungwithme.common.support.Querydsl5RepositorySupport;
import com.mungwithme.user.model.entity.QUser;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.model.entity.UserNotify;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Repository
@Transactional(readOnly = true)
public class UserNotifyDslRepository extends Querydsl5RepositorySupport {


    public UserNotifyDslRepository() {
        super(UserNotify.class);
    }


    /**
     * 특정 알림 검색 API
     *
     * @param id
     * @param currentUser
     * @return
     */
    public UserNotify findByIdAndUser(Long id, User currentUser) {
        return getQueryFactory().selectFrom(userNotify)
            .where(userNotify.id.eq(id).and(userNotify.toUser.eq(currentUser))).fetchOne();
    }



    public Page<UserNotify> findNotifyListByUser(User currentUser,Pageable pageable) {
        JPAQuery<UserNotify> contentQuery = getQueryFactory().selectFrom(userNotify)
            .join(userNotify.toUser)
            .join(userNotify.fromUser)
            .where(userNotify.toUser.eq(currentUser))
            .orderBy(userNotify.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize());
        JPAQuery<Long> countQuery = getQueryFactory().select(userNotify.id.count()).from(userNotify)
            .where(userNotify.toUser.eq(currentUser));
        return applyPagination(pageable, contentQuery, countQuery);
    }




}
