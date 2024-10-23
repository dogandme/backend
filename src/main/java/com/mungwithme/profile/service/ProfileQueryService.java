package com.mungwithme.profile.service;

import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.likes.service.LikesQueryService;
import com.mungwithme.marking.model.dto.sql.MarkingQueryDto;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.model.enums.SortType;
import com.mungwithme.marking.service.marking.MarkingQueryService;
import com.mungwithme.marking.service.marking.MarkingTempService;
import com.mungwithme.marking.service.markingSaves.MarkingSavesQueryService;
import com.mungwithme.pet.model.dto.response.PetInfoResponseDto;
import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.pet.service.PetQueryService;
import com.mungwithme.profile.model.dto.response.ProfileResponseDto;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserFollowsQueryService;
import com.mungwithme.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileQueryService {

    private final UserQueryService userQueryService;
    private final MarkingTempService markingTempService;
    private final PetQueryService petQueryService;
    private final UserFollowsQueryService userFollowsQueryService;
    private final LikesQueryService likesQueryService;
    private final MarkingQueryService markingQueryService;
    private final MarkingSavesQueryService markingSavesQueryService;

    /**
     * 프로필 대시보드 조회
     *
     * @param nickname
     *     nickname
     * @return 프로필 정보
     */
    public ProfileResponseDto findProfileByNickname(String nickname) {

        ProfileResponseDto profileResponseDto = new ProfileResponseDto();

        // 닉네임으로 유저 조회 (없을 시 에러 발생)
        User user = userQueryService.findByNickname(nickname)
            .orElseThrow(() -> new ResourceNotFoundException("error.notfound.nickname"));
        Long userId = user.getId();



        // 본인 프로필 조회 여부
        boolean isSelf = user.getId().equals(userQueryService.findCurrentUser_v2().getId());

        // 조회하는 프로필이 본인일 경우 분기 처리
        if (isSelf) {
            profileResponseDto.setSocialType(user.getSocialType());           // 소셜 로그인 타입
//            profileResponseDto.setTempCnt(
//                markingTempService.countTempMarkingByUserId(userId));     // 임시 저장 수

            profileResponseDto.setBookmarks(markingSavesQueryService.findAllBookmarksIdsByUserId(userId));// 북마크 마킹 목록


            // 마킹 id 목록
            Set<Marking> markingsByUser = markingQueryService.findMarkingsByUser(false, false, userId);
            List<Long> markingsIds = markingsByUser.stream().map(Marking::getId).toList();
            profileResponseDto.setLikes(likesQueryService.findAllLikesIdsByUserId(userId));               // 좋아요 마킹 목록
            profileResponseDto.setMarkings(markingsIds);

        }
        profileResponseDto.setNickname(user.getNickname());                   // 닉네임
        petQueryService.findByUser(user)
            .ifPresent(pet -> profileResponseDto.setPet(
                PetInfoResponseDto.builder()
                    .petId(pet.getId())
                    .name(pet.getName())
                    .description(pet.getDescription())
                    .profile(pet.getProfile())
                    .breed(pet.getBreed())
                    .personalities(pet.getPersonalities())
                    .build()
            )); // 펫 정보
        profileResponseDto.setFollowersIds(userFollowsQueryService.findAllFollowersByUserId(userId));    // 팔로워 목록
        profileResponseDto.setFollowingsIds(userFollowsQueryService.findAllFollowingsByUserId(userId));  // 팔로잉 목록



        return profileResponseDto;
    }
}
