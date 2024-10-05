package com.mungwithme.login.repository;


import com.mungwithme.login.model.entity.LoginStatus;
import com.mungwithme.user.model.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoginStatusRepository extends JpaRepository<LoginStatus, Long> {


    @Query(value =
        "select l from LoginStatus l where l.user = :user and l.sessionId = :sessionId"
            + " and l.loginStatus = :loginStatus and l.defaultLog.isStatus =:isStatus order by l.id asc")
    List<LoginStatus> findList(@Param("user") User user, @Param("loginStatus") Boolean loginStatus,
        @Param("isStatus") Boolean isStatus, @Param("sessionId") String sessionId);



    @Query(value =
        "select l from LoginStatus l where l.user = :user "
            + " and l.loginStatus = :loginStatus and l.defaultLog.isStatus =:isStatus order by l.id asc")
    List<LoginStatus> findList(@Param("user") User user, @Param("loginStatus") Boolean loginStatus,
        @Param("isStatus") Boolean isStatus);

    @Query(value =
        "select l from LoginStatus l where l.user = :user order by l.id asc")
    List<LoginStatus> findList(@Param("user") User user);

    @Modifying(clearAutomatically = true)
    @Query(value =
        "update LoginStatus l set l.loginStatus = :loginStatus , l.defaultLog.isStatus = :isStatus where "
            + "l.user = :user and l.sessionId = :sessionId")
    Integer update(@Param("user") User user, @Param("loginStatus") Boolean loginStatus,
        @Param("isStatus") Boolean isStatus, @Param("sessionId") String sessionId);

    @Modifying(clearAutomatically = true)
    @Query(value =
        "delete from LoginStatus l where l.user =:user ")
    void removeAllByUser(@Param("user") User user);


    @Query(value = "select l from LoginStatus l where l.user = :user and l.redisAuthToken = :redisAuthToken and l.refreshToken = :refreshToken and l.loginStatus = :loginStatus and l.defaultLog.isStatus = :isStatus")
    Optional<LoginStatus> findOne(@Param("user") User user,
        @Param("redisAuthToken") String redisAuthToken, @Param("refreshToken") String refreshToken,
        @Param("loginStatus") Boolean loginStatus, @Param("isStatus") Boolean isStatus);

}
