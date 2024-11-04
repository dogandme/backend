package com.mungwithme.user.model.entity;


import com.mungwithme.common.base.BaseTimeEntity;
import com.mungwithme.user.model.enums.NotifyType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter(AccessLevel.PRIVATE)
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserNotify extends BaseTimeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notify_id")
    private Long id;

    // 알림 컨텐츠 타입 팔로우,좋아요,저장
    @Column(nullable = false)
    private NotifyType notifyType;


    // message code
    @Column(nullable = false)
    private String code;

    // 마킹 아아디
    private Long contentId;

    // 알림 보내는 유저
    @JoinColumn(name = "from_user_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User fromUser;


    @JoinColumn(name = "to_user_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User toUser;

    // 읽음 확인 확인
    // 읽음 : true , 읽지 않음 : false
    @Column(nullable = false)
    private Boolean isRead;



    @Builder
    public UserNotify(Long id, NotifyType notifyType, String code, Long contentId, User fromUser, User toUser,
        Boolean isRead) {
        this.id = id;
        this.notifyType = notifyType;
        this.code = code;
        this.contentId = contentId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.isRead = isRead;
    }

    public static UserNotify create(NotifyType notifyType, User toUser, User fromUser, Long contentId) {
        String code = null;
        // 알림 타입 설정
        switch (notifyType) {
            case LIKE -> code = "notify.like";
            case SAVE -> code = "notify.save";
            default -> code = "notify.follow";
        }
        return UserNotify.builder()
            .code(code)
            .notifyType(notifyType)
            .fromUser(fromUser)
            .toUser(toUser)
            .isRead(false)
            .contentId(contentId).build();
    }

    public void updateIsRead(boolean isRead) {
        this.isRead = isRead;
    }


}
