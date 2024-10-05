package com.mungwithme.user.repository;

import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.model.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAddressRepository extends JpaRepository<UserAddress,Long> {

    @Modifying(clearAutomatically = true)
    @Query(value =
        "delete from UserAddress u where u.user =:user ")
    void deleteAllByUser(@Param("user") User user);
}
