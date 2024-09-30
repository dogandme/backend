package com.mungwithme.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.user.model.dto.sql.UserFollowQueryDto;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.model.entity.UserFollows;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
class UserFollowQueryRepositoryTest {


    @Autowired
    UserFollowQueryRepository userFollowQueryRepository;
    @Test
    void findFollowingUsers() {
        Page<UserFollowQueryDto> followingUsers = userFollowQueryRepository.findFollowingUsers(26L,
            PageRequest.of(0, 10));

        List<UserFollowQueryDto> content = followingUsers.getContent();
        for (UserFollowQueryDto dto : content) {
            User followingUser = dto.getUserFollows().getFollowingUser();
            Pet pet = dto.getPet();

            System.out.println(" ============================================" );
            System.out.println("followerUser.getFollowerUser().getId() = " + followingUser.getId());
            System.out.println("followerUser.getFollowerUser().getNickname() = " + followingUser.getNickname());
            System.out.println("pet.getId() = " + pet.getId());
            System.out.println("pet.getName() = " + pet.getName());
            System.out.println("pet.getProfile() = " + pet.getProfile());


        }
    }



    @Test
    public void findIdsByFollowingUserId() {

    // given
        userFollowQueryRepository.findIdsByFollowerUserId(26L);

    // when

    // then

    }
}