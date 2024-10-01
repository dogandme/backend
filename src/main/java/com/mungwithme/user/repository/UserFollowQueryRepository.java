package com.mungwithme.user.repository;

import com.mungwithme.user.model.dto.sql.UserFollowQueryDto;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.model.entity.UserFollows;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import org.springframework.data.repository.query.Param;

public interface UserFollowQueryRepository extends JpaRepository<UserFollows, Long> {

    @Query("SELECT distinct uf.followerUser.id FROM UserFollows uf "
        + " WHERE uf.followingUser.id = :userId ")
    List<Long> findIdsByFollowingUserId(@Param("userId") long userId);

    @Query("SELECT distinct uf.followingUser.id FROM UserFollows uf "
        + " WHERE uf.followerUser.id = :userId ")
    List<Long> findIdsByFollowerUserId(@Param("userId") Long userId);

    @Query("select f from UserFollows f where f.followingUser = :followingUser and f.followerUser = :followerUser ")
    Optional<UserFollows> findByFollowingUser(
        @org.springframework.data.repository.query.Param("followingUser") User followingUser,
        @org.springframework.data.repository.query.Param("followerUser") User followerUser);


    /**
     * 유저가 팔로잉 한 사용자를 가져오는 query
     *
     * @param userId
     * @return
     */
    @Query("SELECT new com.mungwithme.user.model.dto.sql.UserFollowQueryDto(uf,p) "
        + "FROM UserFollows uf join fetch uf.followingUser join Pet p on p.user = uf.followingUser "
        + " WHERE uf.followerUser.id = :userId ")
    Page<UserFollowQueryDto> findFollowingUsers(@Param("userId") long userId, Pageable pageable);

    /**
     * 유저를 팔로워 하는 사용자를 가져오는 query
     *
     * @param userId
     * @return
     */
    @Query("SELECT new com.mungwithme.user.model.dto.sql.UserFollowQueryDto(uf,p) "
        + "FROM UserFollows uf join fetch uf.followerUser join Pet p on p.user = uf.followerUser "
        + " WHERE uf.followingUser.id = :userId ")
    Page<UserFollowQueryDto> findFollowerUsers(@Param("userId") long userId, Pageable pageable);


}
