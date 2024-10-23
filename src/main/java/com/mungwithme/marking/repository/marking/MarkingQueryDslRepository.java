package com.mungwithme.marking.repository.marking;


import static com.mungwithme.likes.model.entity.QMarkingLikes.markingLikes;
import static com.mungwithme.marking.model.entity.QMarking.marking;
import static com.mungwithme.marking.model.entity.QMarkingSaves.markingSaves;
import static com.mungwithme.pet.model.entity.QPet.pet;
import static com.mungwithme.user.model.entity.QUserFollows.userFollows;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.common.support.Querydsl5RepositorySupport;
import com.mungwithme.maps.dto.response.LocationBoundsDto;
import com.mungwithme.marking.model.dto.request.MarkingSearchDto;
import com.mungwithme.marking.model.dto.response.MarkRepDto;
import com.mungwithme.marking.model.dto.response.QMarkRepDto;
import com.mungwithme.marking.model.dto.sql.MarkingQueryDto;
import com.mungwithme.marking.model.dto.sql.QMarkingQueryDto;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.model.entity.QMarking;
import com.mungwithme.marking.model.enums.MapViewMode;
import com.mungwithme.marking.model.enums.SortType;
import com.mungwithme.marking.model.enums.Visibility;
import com.mungwithme.user.model.entity.QUser;
import com.mungwithme.user.model.entity.QUserFollows;
import com.mungwithme.user.model.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


@Slf4j
@Repository
public class MarkingQueryDslRepository extends Querydsl5RepositorySupport {

    public MarkingQueryDslRepository() {
        super(Marking.class);
    }


    /**
     * 내 마킹 혹은 상대 마킹 (거리순,최신순,인기순)
     * 현재위치중심,현재마킹 중심
     **/
    public Page<MarkingQueryDto> findAllMarkersByUser(
        Set<Address> addressSet,
        MarkingSearchDto markingSearchDto,
        Boolean isDeleted,
        Boolean isTempSaved,
        User currentUser,
        User profileUser,
        Pageable pageable,
        SortType sortType,
        MapViewMode mapViewMode,
        boolean isMyProfile
    ) {
        double lat = markingSearchDto.getLat();
        double lng = markingSearchDto.getLng();
        // Haversine formula를 SQL로 처리

        NumberExpression<Double> distanceExpression = getDistanceExpression(lat, marking.lat, marking.lng, lng);

        JPAQuery<MarkingQueryDto> contentQuery = selectMarkingQueryDto(distanceExpression);

        // 카운트 쿼리 생성
        JPAQuery<Long> countQuery = getQueryFactory().select(
                marking.id.count())
            .from(marking)
            .where(applyFilters(isTempSaved, isDeleted).and(getUserMarkingFilter(profileUser)));

        addJoin(contentQuery)
            .leftJoin(markingSaves).on(marking.eq(markingSaves.marking))
            .leftJoin(markingLikes).on(marking.eq(markingLikes.marking));

        // 전체보기 가 아닌 경우 addressSet 추가
        if (!mapViewMode.equals(MapViewMode.ALL_VIEW) && addressSet != null) {
            contentQuery.where(
                marking.address.in(addressSet)
            );
            countQuery.where(
                marking.address.in(addressSet)
            );
        }

        // userId,삭제여부,임시저장 여부 체크
        contentQuery.where(applyFilters(isTempSaved, isDeleted).and(getUserMarkingFilter(profileUser)));

        // 나의 프로필이 아닌 경우
        // Follow 여부 확인 및 공개 권한 여부 쿼리 추가
        if (!isMyProfile) {

            applyUserFollowsLeftJoin(contentQuery, currentUser);
            applyUserFollowsLeftJoin(countQuery, currentUser);

            // 비공개가 아니며 팔로우 되어있는 경우
            // 혹은 public 인 경우
            contentQuery.where(
                isVisibleCondition()
            );

            countQuery.where(
                isVisibleCondition()
            );
        }
        contentQuery.groupBy(marking.id, pet.id);
        // Order By
        setOrderSortType(lat, lng, sortType, contentQuery);
        contentQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        return applyPagination(pageable, contentQuery, countQuery);
    }










    /**
     *
     *
     * 내 마킹 혹은 상대 마킹 (간략하게)
     *
     *
     *
     *
     * */
    public Page<MarkRepDto> findAllMarksByUser(
        Boolean isDeleted,
        Boolean isTempSaved,
        User currentUser,
        User profileUser,
        Pageable pageable,
        boolean isMyProfile
    ) {
        JPAQuery<MarkRepDto> contentQuery = getQueryFactory().select(
            new QMarkRepDto(marking.id, marking.previewImage, marking.lat, marking.lng)
        ).from(marking);


        // 카운트 쿼리 생성
        JPAQuery<Long> countQuery = getQueryFactory().select(
                marking.id.count())
            .from(marking)
            .where(applyFilters(isTempSaved, isDeleted).and(getUserMarkingFilter(profileUser)));


        // userId,삭제여부,임시저장 여부 체크
        contentQuery.where(applyFilters(isTempSaved, isDeleted).and(getUserMarkingFilter(profileUser)));

        // 나의 프로필이 아닌 경우
        // Follow 여부 확인 및 공개 권한 여부 쿼리 추가
        if (!isMyProfile) {

            applyUserFollowsLeftJoin(contentQuery, currentUser);
            applyUserFollowsLeftJoin(countQuery, currentUser);

            // 비공개가 아니며 팔로우 되어있는 경우
            // 혹은 public 인 경우
            contentQuery.where(
                isVisibleCondition()
            );

            countQuery.where(
                isVisibleCondition()
            );
        }
        contentQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        return applyPagination(pageable, contentQuery, countQuery);
    }




    /**
     * 읍면동 별 마킹 갯수 검색
     *
     * @param currentUser
     * @param addressSet
     * @return
     */
    public List<MarkingQueryDto> findCountBySubDistrict(
        User currentUser, Set<Address> addressSet) {

        JPAQuery<MarkingQueryDto> contentQuery = getQueryFactory().select(
            new QMarkingQueryDto(marking.address.id, marking.id.count())).from(marking);

        contentQuery.where(applyFilters(false, false));

        contentQuery.where(marking.address.in(addressSet));

        // 회원인 경우
        if (currentUser != null) {
            applyUserFollowsLeftJoin(contentQuery, currentUser);

            contentQuery.where(
                isVisibleCondition()
            );
        } else { // 비회원
            contentQuery.where(
                marking.isVisible.eq(Visibility.PUBLIC)
            );
        }

        return contentQuery.groupBy(marking.address).fetch();
    }


    /**
     * 바운더리 안에 있는 마커 출력 API
     *
     * @param locationBoundsDto
     * @param currentUser
     * @return
     */
    public List<MarkRepDto> findMarksByBound(LocationBoundsDto locationBoundsDto, User currentUser) {

        JPAQuery<MarkRepDto> contentQuery = getQueryFactory().select(
            new QMarkRepDto(marking.id, marking.previewImage, marking.lat, marking.lng)
        ).from(marking);

        if (currentUser != null) {
            applyUserFollowsLeftJoin(contentQuery, currentUser);
            contentQuery.where(
                isVisibleCondition()
            );
        } else {
            contentQuery.where(
                marking.isVisible.eq(Visibility.PUBLIC)
            );
        }
        contentQuery.where(applyFilters(false, false).and(
            marking.lat.between(locationBoundsDto.getSouthBottomLat(), locationBoundsDto.getNorthTopLat())
                .and(marking.lng.between(locationBoundsDto.getSouthLeftLng(), locationBoundsDto.getNorthRightLng()))
        ));

        return contentQuery.fetch();
    }


    /**
     * 내 마커 출력 API
     *
     * @param currentUser
     * @return
     */
    public List<MarkRepDto> findMyMarksByBound(User currentUser) {

        JPAQuery<MarkRepDto> contentQuery = getQueryFactory().select(
            new QMarkRepDto(marking.id, marking.previewImage, marking.lat, marking.lng)
        ).from(marking);

        contentQuery.where(applyFilters(false, false).and(
            marking.user.eq(currentUser)
        ));

        return contentQuery.fetch();
    }


    /**
     * isVisible 확인 query
     *
     * @return
     */
    public BooleanExpression isVisibleCondition() {
        return marking.isVisible.ne(Visibility.PRIVATE)
            .and(marking.isVisible.eq(Visibility.PUBLIC)
                .or(userFollows.id.isNotNull().and(marking.isVisible.eq(Visibility.FOLLOWERS_ONLY))));
    }


    /**
     * 팔로우 LeftJoin
     *
     * @param query
     * @param currentUser
     * @return
     */
    public void applyUserFollowsLeftJoin(JPAQuery<?> query, User currentUser) {
        query.leftJoin(userFollows)
            .on(userFollows.followingUser.eq(marking.user)
                .and(userFollows.followerUser.eq(currentUser)));
    }

    /**
     * 임시저장,삭제여부
     *
     * @param isTempSaved
     * @param isDeleted
     * @return
     */
    private BooleanBuilder applyFilters(Boolean isTempSaved, Boolean isDeleted) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(getBooleanEq(marking.isTempSaved, isTempSaved))
            .and(getBooleanEq(marking.isDeleted, isDeleted));
        return builder;
    }

    private BooleanBuilder getUserMarkingFilter(User user) {
        BooleanBuilder builder = new BooleanBuilder();
        if (user != null) {
            return builder.and(getUserEq(user.getId()));
        }
        return builder;
    }

    private void setOrderSortType(Double lat, Double lng, SortType sortType, JPAQuery<MarkingQueryDto> contentQuery) {
        if (sortType.equals(SortType.DISTANCE)) {
            contentQuery.orderBy(getDistanceExpression(lat, marking.lat, marking.lng, lng).asc());
        } else if (sortType.equals(SortType.POPULARITY)) {
            contentQuery.orderBy(markingLikes.id.count().coalesce(0L).desc());
        } else {
            contentQuery.orderBy(marking.regDt.desc());
        }
    }

    private static BooleanExpression getUserEq(Long userId) {
        return marking.user.id.eq(userId);
    }

    private static BooleanExpression getBooleanEq(BooleanPath marking, Boolean isTempSaved) {
        return marking.eq(isTempSaved);
    }


    private JPAQuery<MarkingQueryDto> addJoin(
        JPAQuery<MarkingQueryDto> contentQuery) {
        return contentQuery.join(marking.user).fetchJoin()
            .join(marking.address).fetchJoin()
            .join(pet).on(pet.user.eq(marking.user));
    }

    private JPAQuery<MarkingQueryDto> selectMarkingQueryDto(
        NumberExpression<Double> distanceExpression) {

        return getQueryFactory().select(new QMarkingQueryDto(marking,
            pet,
            markingLikes.id.count().coalesce(0L),
            markingSaves.id.count().coalesce(0L),
            distanceExpression
        )).from(marking);
    }


    private static NumberTemplate<Double> getDistanceExpression(double lat1, NumberPath<Double> lat2,
        NumberPath<Double> lng3, double lng4) {
        return Expressions.numberTemplate(Double.class,
            "(6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({0})) * sin(radians({1}))))",
            lat1, lat2, lng3, lng4);
    }


}