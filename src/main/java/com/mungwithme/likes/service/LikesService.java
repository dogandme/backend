package com.mungwithme.likes.service;


import com.mungwithme.likes.model.dto.response.LikeCountResponseDto;
import com.mungwithme.likes.model.entity.Likes;
import com.mungwithme.likes.model.enums.ContentType;
import com.mungwithme.likes.repository.LikesRepository;
import com.mungwithme.marking.model.enums.Visibility;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.service.marking.MarkingQueryService;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserFollowService;
import com.mungwithme.user.service.UserQueryService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikesService {

    private final UserQueryService userQueryService;
    private final MarkingQueryService markingQueryService;
    private final UserFollowService userFollowService;
    private final LikesRepository likesRepository;

    /**
     * 좋아요 추가
     * 좋아요 삭제
     */
    /**
     * contentType에 따라서 좋아요를 추가하는 API
     *
     * @param contentId
     *     contentId
     * @param contentType
     *     contentType
     */
    @Transactional
    public void addLikes(long contentId, ContentType contentType) {
        User currentUser = userQueryService.findCurrentUser();

        User postUser = null;
        boolean isOwner = false;
        // 좋아요가 이미 있는지 확인
        if (existsLikes(currentUser, contentType, contentId)) {
            throw new IllegalArgumentException("error.arg.exists.likes");
        }
        if (contentType.equals(ContentType.MARKING)) {
            Marking marking = markingQueryService.findById(contentId, false, false);
            postUser = marking.getUser();
            isOwner = postUser.getEmail().equals(currentUser.getEmail());
            Visibility visibility = marking.getIsVisible();
            // 작성자가 아닌 경우에만 공개 범위 확인
            if (!isOwner) {
                switch (visibility) {
                    case PRIVATE:
                        throw new IllegalArgumentException("error.arg.visible.likes");
                    case FOLLOWERS_ONLY:
                        if (userFollowService.existsFollowing(currentUser, postUser)) {
                            throw new IllegalArgumentException("error.arg.visible.likes");
                        }
                        break;
                    default:
                        // PUBLIC일 경우 처리하지 않음
                        break;
                }
            }
        }

        // content null 방지를 위한 Null 체크
        if (postUser != null) {
            Likes likes = Likes.create(currentUser, contentId, contentType);
            likesRepository.save(likes);
        }
    }

    /**
     * 좋아요 추가
     * 좋아요 삭제
     */
    /**
     * 좋아요 취소 API
     *
     * @param contentId
     *     contentId
     * @param contentType
     *     contentType
     */
    @Transactional
    public void removeLikes(long contentId, ContentType contentType) {
        User currentUser = userQueryService.findCurrentUser();

        Likes likes = findLikes(currentUser, contentType, contentId);

        if (likes == null) {
            return;
        }

        likesRepository.delete(likes);
    }


    /**
     * 컨텐트에 해당하는 like 전부 다 삭제
     *
     * @param contentId
     * @param contentType
     */
    @Transactional
    public void removeAllLikes(long contentId, ContentType contentType) {
        likesRepository.deleteAllByContentId(contentId, contentType);
    }


    /**
     * 컨텐트에 해당하는 like 전부 다 삭제
     *
     *
     * @param contentType
     */
    /**
     * 컨텐트 아이디 리스트에 해당하는 like 전부 다 삭제
     * @param contentsIds
     *          아이디 리스트
     * @param contentType
     *          컨텐츠 타입
     */
    @Transactional
    public void removeAllLikes(Set<Long> contentsIds, ContentType contentType) {
        likesRepository.deleteAllByContentIds(contentsIds, contentType);
    }


    /**
     * 유저 좋아요 모두 삭제
     * @param user
     */
    @Transactional
    public void removeAllByUser(User user) {
        likesRepository.deleteAllByUser(user);
    }



    /**
     * 좋아요 검색
     *
     * @param user
     * @param contentType
     * @param contentId
     * @return
     */
    public Likes findLikes(User user, ContentType contentType, long contentId) {
        return likesRepository.fetchLikes(user, contentType, contentId).orElse(null);
    }

    /**
     * 좋아요 검색
     *
     * @return
     */
    public Set<LikeCountResponseDto> findLikeCounts(Set<Long> contentIds, ContentType contentType) {
        return likesRepository.fetchLikeCounts(contentIds, contentType);
    }

    public boolean existsLikes(User user, ContentType contentType, long contentId) {
        Likes likes = findLikes(user, contentType, contentId);
        return likes != null;
    }

}
