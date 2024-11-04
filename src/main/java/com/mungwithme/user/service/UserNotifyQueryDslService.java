package com.mungwithme.user.service;


import com.mungwithme.marking.service.marking.MarkingQueryDslService;
import com.mungwithme.marking.service.marking.MarkingQueryService;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.model.entity.UserNotify;
import com.mungwithme.user.model.enums.NotifyType;
import com.mungwithme.user.repository.UserNotifyDslRepository;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserNotifyQueryDslService {


    private final UserNotifyDslRepository userNotifyDslRepository;
    private final UserQueryService userQueryService;
//    private final MarkingQueryDslService markingQueryDslService;
    private final MessageSource ms;

    public UserNotify findByIdAndUser(Long id, User user) {
        return userNotifyDslRepository.findByIdAndUser(id, user);
    }


    public void findNotifyListByUser(int offset, Locale locale) {
        User currentUser = userQueryService.findCurrentUser();
        return;
    }


    public void findNotifyListByUser(User currentUser, int offset, Locale locale) {
        int pageSize = 20;

        PageRequest pageRequest = PageRequest.of(offset, pageSize);

        Page<UserNotify> notifyListByUser = userNotifyDslRepository.findNotifyListByUser(currentUser, pageRequest);

        // type 별 분류
        List<UserNotify> notifies = notifyListByUser.getContent();

        Set<Long> markingIds = notifies.stream().filter(
                notify -> notify.getNotifyType().equals(NotifyType.SAVE) || notify.getNotifyType().equals(NotifyType.LIKE))
            .map(UserNotify::getContentId).collect(Collectors.toSet());


        return;
    }


}
