package com.mungwithme.user.service;


import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.model.entity.UserNotify;
import com.mungwithme.user.repository.UserNotifyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserNotifyService {


    private final UserNotifyRepository userNotifyRepository;
    private final UserNotifyQueryDslService userNotifyQueryService;
    private final UserQueryService userQueryService;

    /**
     * 알림 추가 API
     *
     * @param userNotify
     */
    @Transactional
    public void addNotify(UserNotify userNotify) {
        userNotifyRepository.save(userNotify);
    }

    /**
     * 알림 삭제 API
     *
     * @param id
     */
    @Transactional
    public void removeNotify(Long id) {
        User currentUser = userQueryService.findCurrentUser();

        UserNotify userNotify = userNotifyQueryService.findByIdAndUser(id, currentUser);

        if (userNotify == null) {
            throw new IllegalArgumentException("error.arg");
        }
        userNotifyRepository.delete(userNotify);
    }


    /**
     * 알림 읽음 처리 API
     *
     * @param id
     */
    @Transactional
    public void editIsRead(Long id,Boolean isRead) {
        User currentUser = userQueryService.findCurrentUser();

        UserNotify userNotify = userNotifyQueryService.findByIdAndUser(id, currentUser);

        if (userNotify == null) {
            throw new IllegalArgumentException("error.arg");
        }
        userNotify.updateIsRead(isRead);
    }




    /**
     * 유저에게 온 알림 전체 삭제 API
     */
    @Transactional
    public void removeAllByToUser() {
        User currentUser = userQueryService.findCurrentUser();
        removeAllByToUser(currentUser);
    }

    /**
     * 유저에게 온 알림 전체 삭제 API
     */
    @Transactional
    public void removeAllByToUser(User currentUser) {
        userNotifyRepository.removeAllByToUser(currentUser);
    }


    /**
     * 해당 유저의 알림 전부 삭제 API
     */
    @Transactional
    public void removeAllByUser(User currentUser) {
        userNotifyRepository.removeAllByUser(currentUser);
    }


}
