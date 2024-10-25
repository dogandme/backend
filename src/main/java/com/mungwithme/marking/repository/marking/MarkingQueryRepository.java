package com.mungwithme.marking.repository.marking;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.marking.model.dto.sql.MarkingQueryDto;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.user.model.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MarkingQueryRepository extends JpaRepository<Marking, Long> {

    String HAVERSINE_FORMULA = "(6371 * acos(cos(radians(:lat)) * cos(radians(m.lat)) *" +
        " cos(radians(m.lng) - radians(:lng)) + sin(radians(:lat)) * sin(radians(m.lat))))  ";


    @Query("select distinct m from Marking m join fetch m.user left join fetch m.images where m.id = :id and m.isDeleted = :isDeleted and m.isTempSaved =:isTempSaved")
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
    

    @Query(
        "SELECT distinct new com.mungwithme.marking.model.dto.sql.MarkingQueryDto(m,p) FROM Marking m "
            + " join fetch m.user "
            + " join fetch Pet p on p.user = m.user "
            + " left join fetch UserFollows f on f.followingUser = m.user and f.followerUser =:user "
            + " left join fetch m.images "
            + " left join fetch m.saves "
            + " WHERE ( m.lat BETWEEN :southBottomLat AND :northTopLat AND m.lng BETWEEN :southLeftLng AND :northRightLng "
            + " and m.isTempSaved =:isTempSaved "
            + " and m.isDeleted =:isDeleted) "
            + " and ( (m.isVisible = 'PUBLIC') "
            + " or (m.user = :user) "
            + " or (m.user =:user and m.isVisible = 'PRIVATE' ) "
            + " or (f.id is not null and m.isVisible = 'FOLLOWERS_ONLY' )) ")
    Set<MarkingQueryDto> findNearbyMarkers(
        @Param("southBottomLat") double southBottomLat,
        @Param("northTopLat") double northTopLat,
        @Param("southLeftLng") double southLeftLng,
        @Param("northRightLng") double northRightLng,
        @Param("isDeleted") boolean isDeleted,
        @Param("isTempSaved") boolean isTempSaved,
        @Param("user") User user
    );


    /**
     * 인기순으로 정렬 (회원 전용)
     *
     * @param addresses
     * @param isDeleted
     * @param isTempSaved
     * @param user
     * @return
     */
    @Query(
        value =
            "select new com.mungwithme.marking.model.dto.sql.MarkingQueryDto(m,p, COALESCE(count(l),0)  ,COALESCE(count(s),0),"
                + HAVERSINE_FORMULA + "  ) "
                + "from Marking m "
                + " join fetch m.user "
                + " join fetch m.address "
                + " join Pet p on p.user = m.user "
                + "left join UserFollows f on f.followingUser = m.user and f.followerUser =:user "
                + "left join MarkingSaves s on m = s.marking "
                + "left join MarkingLikes l on m = l.marking "
                + " WHERE (m.address in (:addresses) "
                + " and m.isTempSaved =:isTempSaved "
                + " and m.isDeleted =:isDeleted) "
                + " and ( (m.isVisible = 'PUBLIC') "
                + " or (m.user = :user) "
                + " or (m.user =:user and m.isVisible = 'PRIVATE' ) "
                + " or (f.id is not null and m.isVisible = 'FOLLOWERS_ONLY' )) group by m.id,p.id "
                + " order by COALESCE(count(l),0)  desc "
    )
    Page<MarkingQueryDto> findMarkersOrderByLikesDesc(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("addresses") Set<Address> addresses,
        @Param("isDeleted") boolean isDeleted,
        @Param("isTempSaved") boolean isTempSaved,
        @Param("user") User user,
        Pageable pageable
    );


    /**
     * 가까운순 으로 정렬 (회원 전용)
     *
     * @param addresses
     * @param isDeleted
     * @param isTempSaved
     * @param user
     * @return
     */
    @Query(
        value =
            "select new com.mungwithme.marking.model.dto.sql.MarkingQueryDto(m,p, COALESCE(count(l),0)  ,COALESCE(count(s),0),"
                + HAVERSINE_FORMULA + "  ) "
                + "from Marking m "
                + " join fetch m.address "
                + " join fetch m.user "
                + " join Pet p on p.user = m.user "
                + "left join UserFollows f on f.followingUser = m.user and f.followerUser =:user "
                + "left join MarkingSaves s on m = s.marking "
                + "left join MarkingLikes l on m = l.marking "
                + " WHERE (m.address in (:addresses) "
                + " and m.isTempSaved =:isTempSaved "
                + " and m.isDeleted =:isDeleted) "
                + " and ( (m.isVisible = 'PUBLIC') "
                + " or (m.user = :user) "
                + " or (m.user = :user and m.isVisible = 'PRIVATE' ) "
                + " or (f.id is not null and m.isVisible = 'FOLLOWERS_ONLY' )) group by m.id,p.id "
                + " order by " + HAVERSINE_FORMULA + " asc "
    )
    Page<MarkingQueryDto> findMarkersOrderByDistAsc(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("addresses") Set<Address> addresses,
        @Param("isDeleted") boolean isDeleted,
        @Param("isTempSaved") boolean isTempSaved,
        @Param("user") User user,
        Pageable pageable
    );

    /**
     * 최신순으로 정렬 (회원 전용)
     *
     * @param addresses
     * @param isDeleted
     * @param isTempSaved
     * @param user
     * @return
     */
    @Query(
        value =
            "select new com.mungwithme.marking.model.dto.sql.MarkingQueryDto(m,p, COALESCE(count(l),0)  ,COALESCE(count(s),0),"
                + HAVERSINE_FORMULA + "  ) "
                + "from Marking m "
                + " join fetch m.user "
                + " join fetch m.address "
                + " join Pet p on p.user = m.user "
                + "left join UserFollows f on f.followingUser = m.user  and f.followerUser =:user "
                + "left join MarkingSaves s on m = s.marking "
                + "left join MarkingLikes l on m = l.marking "
                + " WHERE (m.address in (:addresses) "
                + " and m.isTempSaved =:isTempSaved "
                + " and m.isDeleted =:isDeleted) "
                + " and ( (m.isVisible = 'PUBLIC') "
                + " or (m.user = :user) "
                + " or (m.user =:user and m.isVisible = 'PRIVATE' ) "
                + " or (f.id is not null and m.isVisible = 'FOLLOWERS_ONLY' )) group by m.id,p.id "
                + " order by m.regDt desc ")
    Page<MarkingQueryDto> findMarkersOrderByRegDtDesc(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("addresses") Set<Address> addresses,
        @Param("isDeleted") boolean isDeleted,
        @Param("isTempSaved") boolean isTempSaved,
        @Param("user") User user,
        Pageable pageable
    );


    /**
     * 최신순으로 정렬 (비 회원 전용)
     *
     * @param addresses
     * @param isDeleted
     * @param isTempSaved
     * @return
     */
    @Query(
        value =
            "select new com.mungwithme.marking.model.dto.sql.MarkingQueryDto(m,p, COALESCE(count(l),0)  ,COALESCE(count(s),0),"
                + HAVERSINE_FORMULA + "  ) "
                + "from Marking m "
                + " join fetch m.user "
                + " join fetch m.address "
                + " join  Pet p on p.user = m.user "
                + "left join MarkingSaves s on m = s.marking "
                + "left join MarkingLikes l on m = l.marking "
                + " WHERE (m.address in (:addresses) "
                + " and m.isTempSaved =:isTempSaved "
                + " and m.isDeleted =:isDeleted) "
                + " and m.isVisible = 'PUBLIC' "
                + " group by m.id,p.id "
                + " order by m.regDt desc ")
    Page<MarkingQueryDto> findMarkersOrderByRegDtDesc(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("addresses") Set<Address> addresses,
        @Param("isDeleted") boolean isDeleted,
        @Param("isTempSaved") boolean isTempSaved,
        Pageable pageable
    );









    /**
     * 내 마킹 불러오기 거리 순 정렬 (내 자신 마킹)
     *
     * @param isDeleted
     * @param isTempSaved
     * @param userId
     * @return
     */
    @Query(
        value =
            "select new com.mungwithme.marking.model.dto.sql.MarkingQueryDto(m,p, COALESCE(count(l),0)  ,COALESCE(count(s),0),"
                + HAVERSINE_FORMULA + "  ) "
                + "from Marking m "
                + " join fetch m.user "
                + " join fetch m.address "
                + " join Pet p on p.user = m.user "
                + " left join MarkingSaves s on m = s.marking "
                + " left join MarkingLikes l on m = l.marking "
                + " WHERE m.isTempSaved =:isTempSaved "
                + " and m.isDeleted =:isDeleted "
                + " and m.user.id = :userId "
                + " group by m.id,p.id "
                + " order by COALESCE(count(l),0)  desc ")
    Page<MarkingQueryDto> findAllMarkersByUserLikesDesc(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("isDeleted") boolean isDeleted,
        @Param("isTempSaved") boolean isTempSaved,
        @Param("userId") long userId,
        Pageable pageable
    );





    @Query("SELECT m FROM Marking m "
        + " left join fetch m.images "
        + " left join fetch m.saves "
        + " WHERE m.isDeleted =:isDeleted and m.user.id =:userId ")
    Set<Marking> findAll(@Param("userId") long userId, @Param("isDeleted") boolean isDeleted);


    int countByUserId(Long userId);


    @Query("select m from Marking  m where m.isDeleted = :isDeleted and m.isTempSaved =:isTempSaved and m.user.id  =:userId ")
    Set<Marking> findMarkingsByUser(@Param("isDeleted") boolean isDeleted, @Param("isTempSaved") boolean isTempSaved,
        @Param("userId") long userId);


    int countTempMarkingByUserIdAndIsTempSavedTrue(Long userId);

}
