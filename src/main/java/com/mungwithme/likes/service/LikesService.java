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
import com.mungwithme.user.service.UserService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikesService {

    private final UserService userService;
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
        User currentUser = userService.getCurrentUser();

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
    public void deleteLikes(long contentId, ContentType contentType) {
        User currentUser = userService.getCurrentUser();

        Likes likes = fetchLikes(currentUser, contentType, contentId);

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
    public void deleteAllLikes(long contentId, ContentType contentType) {
        likesRepository.deleteAllByContentId(contentId, contentType);
    }


    /**
     * 좋아요 검색
     *
     * @param user
     * @param contentType
     * @param contentId
     * @return
     */
    public Likes fetchLikes(User user, ContentType contentType, long contentId) {
        return likesRepository.fetchLikes(user, contentType, contentId).orElse(null);
    }

    /**
     * 좋아요 검색
     *
     * @return
     */
    public Set<LikeCountResponseDto> fetchLikeCounts(Set<Long> contentIds, ContentType contentType) {
        return likesRepository.fetchLikeCounts(contentIds, contentType);
    }

    public boolean existsLikes(User user, ContentType contentType, long contentId) {
        Likes likes = fetchLikes(user, contentType, contentId);
        return likes != null;
    }

}
