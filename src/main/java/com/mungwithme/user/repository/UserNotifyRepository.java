package com.mungwithme.user.repository;

import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.model.entity.UserNotify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserNotifyRepository extends JpaRepository<UserNotify, Long> {



    @Modifying(clearAutomatically = true)
    @Query("delete from UserNotify n where n.toUser = :toUser")
    void removeAllByToUser(@Param("toUser") User toUser);



    @Modifying(clearAutomatically = true)
    @Query("delete from UserNotify n where n.toUser = :user or n.fromUser = :user")
    void removeAllByUser(@Param("user") User user);

}
