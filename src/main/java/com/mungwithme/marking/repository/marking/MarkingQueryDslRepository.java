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
import com.mungwithme.marking.model.enums.MapViewMode;
import com.mungwithme.marking.model.enums.SortType;
import com.mungwithme.marking.model.enums.Visibility;
import com.mungwithme.user.model.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.EntityPathBase;
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
     * 동네 마킹 (거리순, 최신순, 인기순)
     *
     * @param currentUser
     * @param addressesSet
     * @param markingSearchDto
     * @param isDeleted
     * @param isTempSaved
     * @param pageable
     * @param sortType
     * @param isMember
     * @return
     */
    public Page<MarkingQueryDto> findNearbyMarkers(
        User currentUser,
        Set<Address> addressesSet,
        MarkingSearchDto markingSearchDto,
        Boolean isDeleted,
        Boolean isTempSaved,
        Pageable pageable,
        SortType sortType,
        Boolean isMember
    ) {
        double lat = markingSearchDto.getLat();
        double lng = markingSearchDto.getLng();

        // 거리 계산 (Haversine formula)
        NumberExpression<Double> distanceExpression = getDistanceExpression(lat, marking.lat, marking.lng, lng);
        JPAQuery<MarkingQueryDto> contentQuery = selectMarkingQueryDto(distanceExpression);
        // 카운트 쿼리 생성
        JPAQuery<Long> countQuery = getQueryFactory()
            .select(marking.id.count())
            .from(marking);

        // 조인 및 조건 추가
        addJoinsAndConditions(contentQuery, countQuery, addressesSet, isTempSaved, isDeleted, currentUser, isMember);

        // 그룹화 및 정렬
        contentQuery.groupBy(marking.id, pet.id);
        setOrderSortType(lat, lng, sortType, contentQuery);
        contentQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());

        // 페이지네이션 적용
        return applyPagination(pageable, contentQuery, countQuery);
    }


    /**
     * 내 마킹 혹은 상대 마킹 (거리순, 최신순, 인기순) - 현재 위치 중심, 현재 마킹 중심
     *
     * @param markingSearchDto
     * @param isDeleted
     * @param isTempSaved
     * @param profileUser
     * @param pageable
     * @param sortType
     * @param mapViewMode
     * @param isMyProfile
     * @param isMyFollowing
     * @return
     */
    public Page<MarkingQueryDto> findAllMarkersByUser(
        LocationBoundsDto locationBoundsDto,
        MarkingSearchDto markingSearchDto,

        Boolean isDeleted,
        Boolean isTempSaved,
        User profileUser,
        Pageable pageable,
        SortType sortType,
        MapViewMode mapViewMode,
        Boolean isMyProfile,
        Boolean isMyFollowing
    ) {
        double lat = markingSearchDto.getLat();
        double lng = markingSearchDto.getLng();

        // 거리 계산 (Haversine formula)
        NumberExpression<Double> distanceExpression = getDistanceExpression(lat, marking.lat, marking.lng, lng);
        JPAQuery<MarkingQueryDto> contentQuery = selectMarkingQueryDto(distanceExpression);

        // 카운트 쿼리 생성
        JPAQuery<Long> countQuery = getQueryFactory()
            .select(marking.id.count())
            .from(marking);

        // 조인 및 조건 추가
        addJoinsAndConditions(locationBoundsDto, contentQuery, countQuery, mapViewMode, isTempSaved, isDeleted,
            profileUser,
            isMyProfile, isMyFollowing);

        // 그룹화 및 정렬
        contentQuery.groupBy(marking.id, pet.id);
        setOrderSortType(lat, lng, sortType, contentQuery);
        contentQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());

        // 페이지네이션 적용
        return applyPagination(pageable, contentQuery, countQuery);
    }


    /**
     * 나의 임시저장 마킹 리스트
     *
     * @param isDeleted
     * @param isTempSaved
     * @param pageable
     * @return
     */
    public Page<MarkingQueryDto> findTempMarkersByUser(
        Boolean isDeleted,
        Boolean isTempSaved,
        User currentUser,
        Pageable pageable
    ) {

        // 거리 계산 (Haversine formula)
        JPAQuery<MarkingQueryDto> contentQuery = getQueryFactory()
            .select(new QMarkingQueryDto(marking))
            .from(marking)
            .where(applyFilters(isTempSaved, isDeleted).and(getUserEq(currentUser.getId())))
            .orderBy(marking.regDt.desc(), marking.id.desc())
            .offset(pageable.getOffset()).limit(pageable.getPageSize());

        // 카운트 쿼리 생성
        JPAQuery<Long> countQuery = getQueryFactory()
            .select(marking.id.count())
            .from(marking).
            where(applyFilters(isTempSaved, isDeleted).and(getUserEq(currentUser.getId())));

        contentQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());

        // 페이지네이션 적용
        return applyPagination(pageable, contentQuery, countQuery);
    }


    /**
     * 이 장소 마킹 API
     *
     * @param isTempSaved
     * @param isDeleted
     * @param currentUser
     * @param locationBoundsDto
     * @param sortType
     * @param pageable
     * @param isMember
     * @return
     */
    public Page<MarkingQueryDto> findMarkingsByBounds(
        Boolean isTempSaved,
        Boolean isDeleted,
        User currentUser,
        LocationBoundsDto locationBoundsDto,
        MarkingSearchDto markingSearchDto,
        SortType sortType,
        Pageable pageable,
        boolean isMember) {

        double lat = markingSearchDto.getLat();
        double lng = markingSearchDto.getLng();

        // 거리 계산 (Haversine formula)
        NumberExpression<Double> distanceExpression = getDistanceExpression(lat, marking.lat, marking.lng, lng);

        // Content Query
        JPAQuery<MarkingQueryDto> contentQuery = selectMarkingQueryDto(distanceExpression);
        // Count Query
        JPAQuery<Long> countQuery = getQueryFactory().select(marking.id.count())
            .from(marking);

        // 각종 마킹 작성쟈와 관련 정보 fetch
        addFetchJoin(contentQuery)
            .leftJoin(markingSaves).on(marking.eq(markingSaves.marking))
            .leftJoin(markingLikes).on(marking.eq(markingLikes.marking))
            .where(
                applyFilters(isTempSaved, isDeleted)
                    .and(getBoundCondition(locationBoundsDto))
                    .and(isMember ? isVisibleCondition(currentUser) : marking.isVisible.eq(Visibility.PUBLIC))
            ).groupBy(marking.id, pet.id);

        // Apply Sorting and Pagination
        setOrderSortType(lat, lng, sortType, contentQuery);
        contentQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());

        countQuery.where(
            applyFilters(isTempSaved, isDeleted)
                .and(getBoundCondition(locationBoundsDto))
                .and(isMember ? isVisibleCondition(currentUser) : marking.isVisible.eq(Visibility.PUBLIC))
        );

        if (isMember) {
            applyUserFollowsLeftJoin(contentQuery, currentUser);
            applyUserFollowsLeftJoin(countQuery, currentUser);
        }

        return applyPagination(pageable, contentQuery, countQuery);
    }

    /**
     * 내 마킹 혹은 상대 마킹 (간략하게)
     */
    public Page<MarkRepDto> findAllMarksByUser(
        Boolean isDeleted,
        Boolean isTempSaved,
        User profileUser,
        Pageable pageable,
        boolean isMyProfile,
        boolean isFollowing
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
            applyVisibilityConditions(contentQuery, countQuery, isFollowing);
        }

        contentQuery.orderBy(marking.regDt.desc(), marking.id.desc());
        contentQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        return applyPagination(pageable, contentQuery, countQuery);
    }


    public Long findTempCount(User currentUser) {
        JPAQuery<Long> countQuery = getQueryFactory()
            .select(marking.id.count())
            .from(marking).
            where(applyFilters(true, false).and(getUserEq(currentUser.getId())));

        return countQuery.fetchOne();
    }


    /**
     * Type 에 따른 좋아요 나 저장 리스트 반환
     *
     * @param isDeleted
     * @param isTempSaved
     * @param currentUser
     * @param pageable
     * @param type
     * @return
     */
    public Page<MarkingQueryDto> findAllTypeMarkersByUser(
        Boolean isDeleted,
        Boolean isTempSaved,
        User currentUser,
        Pageable pageable,
        String type
    ) {

        boolean isType = type.equals("like");

        // type 에 따른 Join 분류
        EntityPathBase<?> joinTarget = isType ? markingLikes : markingSaves;

        // type 에 따른 group by,order 분류
        NumberPath<Long> typeIdTarget = isType ? markingLikes.id : markingSaves.id;

        // type 에 따른 Join 설정 (Like,save)
        BooleanExpression joinCondition =
            isType ? markingLikes.marking.eq(marking).and(markingLikes.user.eq(currentUser))
                : markingSaves.marking.eq(marking).and(markingSaves.user.eq(currentUser));

        QMarkingQueryDto qMarkingQueryDto = null;

        // 공통 count 표현식
        NumberExpression<Long> likeCount = markingLikes.id.count().coalesce(0L);
        NumberExpression<Long> saveCount = markingSaves.id.count().coalesce(0L);

        // 조건에 따라 다른 생성자 사용
        if (isType) {
            qMarkingQueryDto = new QMarkingQueryDto(marking, pet, markingLikes, likeCount, saveCount);
        } else {
            qMarkingQueryDto = new QMarkingQueryDto(marking, pet, markingSaves, likeCount, saveCount);
        }

        JPAQuery<MarkingQueryDto> contentQuery = getQueryFactory().select(qMarkingQueryDto).from(marking);
        addFetchJoin(contentQuery);

        contentQuery.join(joinTarget).on(joinCondition);

        applyUserFollowsLeftJoin(contentQuery, currentUser);

        contentQuery
            .leftJoin(markingSaves).on(marking.eq(markingSaves.marking))
            .leftJoin(markingLikes).on(marking.eq(markingLikes.marking))
            .where(applyFilters(isTempSaved, isDeleted).and(isVisibleCondition(currentUser)));

        // Group by
        contentQuery.groupBy(marking.id, pet.id, typeIdTarget)
            .orderBy(typeIdTarget.desc());

        contentQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());

        // 카운트 쿼리
        JPAQuery<Long> countQuery = getQueryFactory().select(marking.id.count()).from(marking);

        countQuery.join(joinTarget).on(joinCondition);

        applyUserFollowsLeftJoin(countQuery, currentUser);
        countQuery.where(applyFilters(isTempSaved, isDeleted).and(isVisibleCondition(currentUser)));

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

        boolean isMember = currentUser != null;
        // 회원인 경우

        if (isMember) {
            applyUserFollowsLeftJoin(contentQuery, currentUser);
        }

        contentQuery.where(isMember ? isVisibleCondition(currentUser) : marking.isVisible.eq(Visibility.PUBLIC));

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

        boolean isMember = currentUser != null;
        JPAQuery<MarkRepDto> contentQuery = getQueryFactory().select(
            new QMarkRepDto(marking.id, marking.previewImage, marking.lat, marking.lng)
        ).from(marking);

        if (isMember) {
            applyUserFollowsLeftJoin(contentQuery, currentUser);
        }

        contentQuery.where(applyFilters(false, false).and(
            getBoundCondition(locationBoundsDto)
        ).and(isMember ? isVisibleCondition(currentUser) : marking.isVisible.eq(Visibility.PUBLIC)));

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
     * isOnlyUser = 특정 유저 마킹만 검색할건지
     *
     * @param contentQuery
     * @param countQuery
     * @param addressSet
     * @param isTempSaved
     * @param isDeleted
     * @param currentUser
     */
    private void addJoinsAndConditions(
        JPAQuery<MarkingQueryDto> contentQuery,
        JPAQuery<Long> countQuery,
        Set<Address> addressSet,
        Boolean isTempSaved,
        Boolean isDeleted,
        User currentUser,
        boolean isMember
    ) {
        // 기본 조인 설정
        addFetchJoin(contentQuery)
            .leftJoin(markingSaves).on(marking.eq(markingSaves.marking))
            .leftJoin(markingLikes).on(marking.eq(markingLikes.marking));

        BooleanExpression addressCondition = marking.address.in(addressSet);
        contentQuery.where(addressCondition);
        countQuery.where(addressCondition);

        // 삭제 여부, 임시 저장 여부
        BooleanBuilder baseConditions = applyFilters(isTempSaved, isDeleted);
        contentQuery.where(baseConditions);
        countQuery.where(baseConditions);

        // 회원이 아닌 경우 following join
        if (isMember) {
            applyUserFollowsLeftJoin(contentQuery, currentUser);
            applyUserFollowsLeftJoin(countQuery, currentUser);

            BooleanExpression visibilityCondition = isVisibleCondition(currentUser);

            contentQuery.where(visibilityCondition);
            countQuery.where(visibilityCondition);
        }


    }

    /**
     * @param contentQuery
     * @param countQuery
     * @param mapViewMode
     * @param isTempSaved
     * @param isDeleted
     * @param profileUser
     * @param isMyProfile
     *     나의 프로필인지
     * @param isFollowing
     *     팔로우 여부
     */
    private void addJoinsAndConditions(
        LocationBoundsDto locationBoundsDto,
        JPAQuery<MarkingQueryDto> contentQuery,
        JPAQuery<Long> countQuery,
        MapViewMode mapViewMode,
        Boolean isTempSaved,
        Boolean isDeleted,
        User profileUser,
        boolean isMyProfile,
        boolean isFollowing
    ) {

        // 기본 조인 설정
        addFetchJoin(contentQuery)
            .leftJoin(markingSaves).on(marking.eq(markingSaves.marking))
            .leftJoin(markingLikes).on(marking.eq(markingLikes.marking));

        // 전체 보기 모드가 아닌 경우 addressSet 필터 추가
        if (!MapViewMode.ALL_VIEW.equals(mapViewMode)) {
            BooleanExpression boundCondition = getBoundCondition(locationBoundsDto);
            contentQuery.where(boundCondition);
            countQuery.where(boundCondition);
        }

        // 삭제 여부, 임시 저장 여부, userId 조건 적용
        BooleanBuilder baseConditions = applyFilters(isTempSaved, isDeleted)
            .and(getUserMarkingFilter(profileUser));
        contentQuery.where(baseConditions);
        countQuery.where(baseConditions);

        // 내 프로필이 아닌 경우 Follow 및 공개 권한 조건 추가
        if (!isMyProfile) {
            applyVisibilityConditions(contentQuery, countQuery, isFollowing);
        }

    }

    private <T> void applyVisibilityConditions(JPAQuery<T> contentQuery, JPAQuery<Long> countQuery,
        boolean isFollowing) {
        // 팔로우 여부에 따른 공개 조건 설정
        contentQuery.where(
            isFollowing ? marking.isVisible.in(Visibility.FOLLOWERS_ONLY, Visibility.PUBLIC) :
                marking.isVisible.eq(Visibility.PUBLIC)
        );
        countQuery.where(
            isFollowing ? marking.isVisible.in(Visibility.FOLLOWERS_ONLY, Visibility.PUBLIC) :
                marking.isVisible.eq(Visibility.PUBLIC)
        );
    }


    /**
     * 바운더리 값 비교
     *
     * @return
     */
    private BooleanExpression getBoundCondition(LocationBoundsDto locationBoundsDto) {
        return marking.lat.between(locationBoundsDto.getSouthBottomLat(), locationBoundsDto.getNorthTopLat())
            .and(marking.lng.between(locationBoundsDto.getSouthLeftLng(), locationBoundsDto.getNorthRightLng()));
    }


    /**
     * 공개 권한 값 비교
     *
     * @param currentUser
     * @return
     */
    private BooleanExpression isVisibleCondition(User currentUser) {
        return marking.isVisible.eq(Visibility.PUBLIC)
            .or(currentUser != null ? marking.user.eq(currentUser)
                : Expressions.booleanTemplate("true"))  // null 대신 TRUE 조건
            .or(userFollows.id.isNotNull().and(marking.isVisible.eq(Visibility.FOLLOWERS_ONLY)));
    }


    /**
     * 팔로우 LeftJoin
     *
     * @param query
     * @param currentUser
     * @return
     */
    private void applyUserFollowsLeftJoin(JPAQuery<?> query, User currentUser) {
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

    /**
     * 작성자 비교
     *
     * @param user
     * @return
     */
    private BooleanBuilder getUserMarkingFilter(User user) {
        BooleanBuilder builder = new BooleanBuilder();
        if (user != null) {
            return builder.and(getUserEq(user.getId()));
        }
        return builder;
    }

    /**
     * 거리순,인기순, 최신수 order Type
     *
     * @param lat
     * @param lng
     * @param sortType
     * @param contentQuery
     */
    private void setOrderSortType(Double lat, Double lng, SortType sortType, JPAQuery<MarkingQueryDto> contentQuery) {
        if (sortType.equals(SortType.DISTANCE)) {
            contentQuery.orderBy(getDistanceExpression(lat, marking.lat, marking.lng, lng).asc());
        } else if (sortType.equals(SortType.POPULARITY)) {
            contentQuery.orderBy(markingLikes.id.count().coalesce(0L).desc(), marking.regDt.desc(), marking.id.desc());
        } else {
            contentQuery.orderBy(marking.regDt.desc(), marking.id.desc());
        }
    }

    private static BooleanExpression getUserEq(Long userId) {
        return marking.user.id.eq(userId);
    }

    private static BooleanExpression getBooleanEq(BooleanPath marking, Boolean isTempSaved) {
        return marking.eq(isTempSaved);
    }


    /**
     * fetchJoin 모음
     *
     * @param contentQuery
     * @return
     */
    private JPAQuery<MarkingQueryDto> addFetchJoin(
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

    private JPAQuery<MarkingQueryDto> selectMarkingQueryDto() {

        return getQueryFactory().select(
            new QMarkingQueryDto(marking,
                pet,
                markingLikes.id.count().coalesce(0L),
                markingSaves.id.count().coalesce(0L)
            )).from(marking);
    }


    /**
     * 나의 위치에서 마킹의 거리계산
     *
     * @param lat1
     * @param lat2
     * @param lng3
     * @param lng4
     * @return
     */
    private static NumberTemplate<Double> getDistanceExpression(double lat1, NumberPath<Double> lat2,
        NumberPath<Double> lng3, double lng4) {
        return Expressions.numberTemplate(Double.class,
            "(6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({0})) * sin(radians({1}))))",
            lat1, lat2, lng3, lng4);
    }


}
