package com.mungwithme.marking.service.markingSaves;


import com.mungwithme.likes.model.entity.Likes;
import com.mungwithme.likes.model.enums.ContentType;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.model.entity.MarkingSaves;
import com.mungwithme.marking.model.enums.Visibility;
import com.mungwithme.marking.repository.markingSaves.MarkingSavesRepository;
import com.mungwithme.marking.service.marking.MarkingQueryService;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserFollowService;
import com.mungwithme.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MarkingSavesService {


    private final UserService userService;
    private final UserFollowService userFollowService;
    private final MarkingQueryService markingQueryService;
    private final MarkingSavesRepository markingSavesRepository;

    /**
     * 마킹을 즐겨찾기 하는 API
     * @param markingId
     */
    @Transactional
    public void addSaves(long markingId) {
        User currentUser = userService.getCurrentUser();
        User postUser = null;
        boolean isOwner = false;
        Marking marking = markingQueryService.findById(markingId, false, false);
        // 이미 즐겨찾기를 하였는지 확인
        if (existsSaves(currentUser, marking)) {
            throw new IllegalArgumentException("ex) 이미 저장을 한 컨텐츠입니다.");
        }
        postUser = marking.getUser();
        isOwner = postUser.getEmail().equals(currentUser.getEmail());
        Visibility visibility = marking.getIsVisible();
        // 작성자가 아닌 경우에만 공개 범위 확인
        if (!isOwner) {
            switch (visibility) {
                case PRIVATE:
                    throw new IllegalArgumentException("ex) 비공개 마킹은 저장을 할수가 없습니다.");
                case FOLLOWERS_ONLY:
                    if (userFollowService.existsFollowing(currentUser, postUser)) {
                        throw new IllegalArgumentException("ex) 팔로우만 저장을 누를 수 있습니다.");
                    }
                    break;
                default:
                    // PUBLIC일 경우 처리하지 않음
                    break;
            }
        }
        MarkingSaves markingSaves = MarkingSaves.create(currentUser, marking);
        markingSavesRepository.save(markingSaves);
    }

    /**
     * 즐겨찾기에서 삭제
     * @param markingId
     */
    @Transactional
    public void deleteSaves(long markingId) {
        User currentUser = userService.getCurrentUser();
        MarkingSaves markingSaves = fetchSaves(currentUser, markingId);
        if (markingSaves == null) {
            throw new IllegalArgumentException("ex) 잘못된 요청 입니다");
        }
        markingSavesRepository.delete(markingSaves);
    }

    /**
     * 마킹 즐겨찾기 전부 삭제
     * @param marking
     */
    @Transactional
    public void deleteAllSaves(Marking marking) {
        markingSavesRepository.deleteAllByMarking(marking);
    }


    public boolean existsSaves(User user, Marking marking) {
        MarkingSaves markingSaves = fetchSaves(user, marking.getId());
        return markingSaves != null;
    }

    public MarkingSaves fetchSaves(User user, long markingId) {
        return markingSavesRepository.fetchSaves(user, markingId).orElse(null);
    }
}
