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
            + " left join fetch m.images "
            + " left join fetch m.saves "
            + " WHERE ( m.lat BETWEEN :southBottomLat AND :northTopLat AND m.lng BETWEEN :southLeftLng AND :northRightLng "
            + " and m.isTempSaved =:isTempSaved "
            + " and m.isDeleted =:isDeleted) "
            + " and ( m.isVisible = 'PUBLIC') ")
    Set<MarkingQueryDto> findNearbyMarkersOnlyPublic(
        @Param("southBottomLat") double southBottomLat,
        @Param("northTopLat") double northTopLat,
        @Param("southLeftLng") double southLeftLng,
        @Param("northRightLng") double northRightLng,
        @Param("isDeleted") boolean isDeleted,
        @Param("isTempSaved") boolean isTempSaved);

//

//    @Query(
//        "SELECT distinct new com.mungwithme.marking.model.dto.request.MarkingTestDto(m, count(l.id)) "
//            + "FROM Marking m "
//            + "left join Likes l on l.contentId = m.id and l.contentType = 'MARKING' "
//            + "join m.user u "
//            + "left join UserFollows f on f.followingUser = m.user "
//            + "left join m.user.pets p "
//            + "left join m.images i "
//            + "WHERE (m.isTempSaved = :isTempSaved AND m.isDeleted = :isDeleted) "
//            + "AND ( (m.isVisible = 'PUBLIC') "
//            + "OR (m.user = :user AND m.isVisible = 'PRIVATE') "
//            + "OR (f.id is not null AND m.isVisible = 'FOLLOWERS_ONLY') ) "
//            + "GROUP BY m.id, u.id, m.region, m.lat, m.lng, m.content, m.isTempSaved, m.isDeleted, m.isVisible, m.regDt, m.modDt")

//    @Query(value = "SELECT * as marking, COUNT(l.likes_id) as likedCount "
//        + "FROM Marking m "
//        + "LEFT JOIN Likes l ON l.content_id = m.id AND l.content_type = 'MARKING' "
//        + "JOIN User u ON u.user_id = m.user_id "
//        + "LEFT JOIN User_Follows f ON f.following_uid = u.user_id "
//        + "LEFT JOIN Mark_Image i ON i.marking_id = m.id "
//        + "WHERE m.is_temp_saved = :isTempSaved "
//        + "AND m.is_deleted = :isDeleted "
//        + "AND (m.is_visible = 'PUBLIC' "
//        + "     OR (m.user_id = :userId AND m.is_visible = 'PRIVATE') "
//        + "     OR (f.id IS NOT NULL AND m.is_visible = 'FOLLOWERS_ONLY')) "
//        + "GROUP BY marking ", nativeQuery = true)


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
     * 인기순으로 정렬 (비 회원 전용)
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
                + " join Pet p on p.user = m.user "
                + "left join MarkingSaves s on m = s.marking "
                + "left join MarkingLikes l on m = l.marking "
                + " WHERE (m.address in (:addresses) "
                + " and m.isTempSaved =:isTempSaved "
                + " and m.isDeleted =:isDeleted) "
                + " and  (m.isVisible = 'PUBLIC') "
                + " group by m.id,p.id "
                + " order by COALESCE(count(l),0)  desc "
    )
    Page<MarkingQueryDto> findMarkersOrderByLikesDesc(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("addresses") Set<Address> addresses,
        @Param("isDeleted") boolean isDeleted,
        @Param("isTempSaved") boolean isTempSaved,
        Pageable pageable
    );


    /**
     * 가까운순 으로 정렬 ( 비 회원 전용)
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
                + " join Pet p on p.user = m.user "
                + "left join MarkingSaves s on m = s.marking "
                + "left join MarkingLikes l on m = l.marking "
                + " WHERE (m.address in (:addresses) "
                + " and m.isTempSaved =:isTempSaved "
                + " and m.isDeleted =:isDeleted) "
                + " and m.isVisible = 'PUBLIC' "
                + " group by m.id,p.id "
                + " order by " + HAVERSINE_FORMULA + " asc "
    )
    Page<MarkingQueryDto> findMarkersOrderByDistAsc(
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
                + " order by " + HAVERSINE_FORMULA + " asc ")
    Page<MarkingQueryDto> findAllMarkersByUserDistAsc(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("isDeleted") boolean isDeleted,
        @Param("isTempSaved") boolean isTempSaved,
        @Param("userId") long userId,
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


    /**
     * 내 마킹 불러오기 최신순 정렬 (내 자신 마킹)
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
                + " order by m.regDt desc ")
    Page<MarkingQueryDto> findAllMarkersByUserRegDtDesc(
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


    @Query("SELECT distinct new com.mungwithme.marking.model.dto.sql.MarkingQueryDto(m,p,l)FROM Marking m "
        + " join fetch m.user "
        + " join MarkingLikes l on l.marking = m and l.user.id = :userId "
        + " join fetch Pet p on p.user.id = :userId "
        + " left join fetch UserFollows f on f.followingUser = m.user "
        + " left join fetch m.images "
        + " left join fetch m.saves "
        + " WHERE m.isDeleted =:isDeleted "
        + "and m.isTempSaved = :isTempSaved   "
        + "and ((m.isVisible = 'PUBLIC') "
        + " or (m.user.id =:userId and m.isVisible = 'PRIVATE' ) "
        + " or (f.id is not null and m.isVisible = 'FOLLOWERS_ONLY' )) ")
    Set<MarkingQueryDto> findAllLikedMarkersByUser(
        @Param("isDeleted") boolean isDeleted,
        @Param("isTempSaved") boolean isTempSaved,
        @Param("userId") long userId);


    @Query("SELECT distinct new com.mungwithme.marking.model.dto.sql.MarkingQueryDto(m,p,s)FROM Marking m "
        + " join fetch m.user "
        + " join MarkingSaves s on s.marking.id = m.id and s.user.id = :userId "
        + " join fetch Pet p on p.user.id = :userId "
        + " left join fetch UserFollows f on f.followingUser = m.user "
        + " left join fetch m.images "
        + " left join fetch m.saves "
        + " WHERE m.isDeleted =:isDeleted "
        + "and m.isTempSaved = :isTempSaved   "
        + "and ((m.isVisible = 'PUBLIC') "
        + " or (m.user.id =:userId and m.isVisible = 'PRIVATE' ) "
        + " or (f.id is not null and m.isVisible = 'FOLLOWERS_ONLY' )) ")
    Set<MarkingQueryDto> findAllSavedMarkersByUser(
        @Param("isDeleted") boolean isDeleted,
        @Param("isTempSaved") boolean isTempSaved,
        @Param("userId") long userId);



    @Query("select count (m.id) from Marking m where m.isDeleted = :isDeleted and m.isTempSaved = :isTempSaved and m.user.id = :userId")
    Long findTempCount(  @Param("isDeleted") boolean isDeleted,
        @Param("isTempSaved") boolean isTempSaved,
        @Param("userId") long userId);
    int countByUserId(Long userId);
}
