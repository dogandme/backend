package com.mungwithme.user.service;

import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.pet.model.dto.response.PetInfoResponseDto;
import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.user.model.dto.response.UserInfoResponseDto;
import com.mungwithme.user.model.dto.sql.UserFollowQueryDto;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.model.entity.UserFollows;
import com.mungwithme.user.repository.UserFollowQueryRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Slf4j

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFollowsQueryService {

    private final UserFollowQueryRepository userFollowQueryRepository;
    private final UserQueryService userQueryService;

    /**
     * 유저의 팔로워 목록 조회
     *
     * @param userId
     *     유저PK
     * @return 팔로워 id 목록
     */
    public List<Long> findAllFollowersByUserId(Long userId) {
        return userFollowQueryRepository.findIdsByFollowingUserId(userId);
    }

    /**
     * 유저의 팔로잉 목록 조회
     *
     * @param userId
     *     유저PK
     * @return 팔로잉 id 목록
     */
    public List<Long> findAllFollowingsByUserId(Long userId) {
        return userFollowQueryRepository.findIdsByFollowerUserId(userId);
    }


    /**
     * 팔로잉 유저 기준으로 검색
     *
     * @return
     */
    public UserFollows findByFollowingUser(User followerUser, User followingUser) {
        return userFollowQueryRepository.findByFollowingUser(followingUser, followerUser).orElse(null);
    }


    /**
     * 유저가 팔로잉 한 사용자의 정보를 가져오는 API
     *
     * @param nickname
     * @return
     */
    public List<UserInfoResponseDto> findFollowingUsers(String nickname, int offset, int size) {
        User user = userQueryService.findByNickname(nickname)
            .orElseThrow(() -> new ResourceNotFoundException("error.notfound.user"));

        PageRequest pageRequest = getPageRequest(offset, size);

        Page<UserFollowQueryDto> queryDtoPage = userFollowQueryRepository.findFollowingUsers(user.getId(),
            pageRequest);

        List<UserFollowQueryDto> userFollowQueryDtoList = queryDtoPage.getContent();

        // 유저 정보와 펫정보를 리스트
        return userFollowQueryDtoList.stream().map(data ->
            {
                UserFollows userFollows = data.getUserFollows();
                Pet pet = data.getPet();

                User followingUser = userFollows.getFollowingUser();
                return buildUserInfoResponseDto(pet, followingUser);
            }
        ).collect(Collectors.toList());
    }

    /**
     * 유저를 팔로우 한 사용자의 정보를 가져오는 API
     *
     * @param nickname
     * @return
     */
    public List<UserInfoResponseDto> findFollowerUsers(String nickname, int offset, int size) {
        User user = userQueryService.findByNickname(nickname)
            .orElseThrow(() -> new ResourceNotFoundException("error.notfound.user"));

        PageRequest pageRequest = getPageRequest(offset, size);

        Page<UserFollowQueryDto> queryDtoPage = userFollowQueryRepository.findFollowerUsers(user.getId(),
            pageRequest);

        List<UserFollowQueryDto> userFollowQueryDtoList = queryDtoPage.getContent();

        // 유저 정보와 펫정보를 리스트
        return userFollowQueryDtoList.stream().map(data ->
            {
                UserFollows userFollows = data.getUserFollows();
                Pet pet = data.getPet();
                User followerUser = userFollows.getFollowerUser();
                return buildUserInfoResponseDto(pet, followerUser);
            }
        ).collect(Collectors.toList());
    }

    private static UserInfoResponseDto buildUserInfoResponseDto(Pet pet, User user) {
        return UserInfoResponseDto.builder()
            .nickname(user.getNickname())
            .userId(user.getId())
            .pet(
                PetInfoResponseDto.builder()
                    .name(pet.getName())
                    .profile(pet.getProfile()).build()
            ).build();
    }

    private static PageRequest getPageRequest(int offset, int size) {
        Sort sort = Sort.by(
            Order.desc("id")
        );
        return PageRequest.of(offset, size, sort);
    }


}
