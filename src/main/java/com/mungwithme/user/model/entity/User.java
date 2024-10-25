package com.mungwithme.user.model.entity;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.common.base.BaseTimeEntity;
import com.mungwithme.user.model.enums.AgeGroup;
import com.mungwithme.user.model.enums.Gender;
import com.mungwithme.user.model.enums.Role;
import com.mungwithme.user.model.enums.SocialType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

/**
 * 회원 ENTITY
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(indexes = {@Index(name = "idx_user_email", columnList = "email", unique = true),
    @Index(name = "idx_user_token", columnList = "token", unique = true)})
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")   // 컬럼명 : user_id
    private Long id;


    @Column(nullable = false)
    private String token;

    // @Column 어노테이션을 명시적으로 지정하지 않아도, JPA는 엔티티 클래스의 필드를 기본적으로 데이터베이스 테이블의 컬럼으로 매핑합니다.
    private String email;           // 이메일(ID)
    private String password;        // 비밀번호
    private String nickname;        // 닉네임
    private int age;                // 나이(추가정보)

    @Enumerated(EnumType.STRING)
    private Gender gender;          // 성별

    @Setter
    @Enumerated(EnumType.STRING)
    private Role role;              // 권한 (기본:GUEST, 선택정보입력시:USER, 관리자:ADMIN)

    @Enumerated(EnumType.STRING)
    private SocialType socialType;  // 소셜 채널(Kakao, Google, Naver)


    // oAuth에서 제공해주는 accessToken 기한 1 시간
    private String oAuthAccessToken;

    // oAuth에서 제공해주는 refreshToken 기한 1년
    private String oAuthRefreshToken;

    // 최신 닉네임 변경 일자
    private LocalDateTime nickLastModDt;

    private Boolean marketingYn;    // 마케팅 수신 동의 여부
    private Boolean persistLogin;   // 로그인 유지 여부


    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private Set<UserAddress> userAddresses;

    /**
     * 비밀번호 암호화 메소드
     *
     * @param passwordEncoder
     *     비밀번호 인코더
     */
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }


    /**
     * 비밀번호 업데이트
     *
     * @param password
     * @param passwordEncoder
     * @return
     */
    public void updatePw(String password, PasswordEncoder passwordEncoder) {
        this.setPassword(passwordEncoder.encode(password));
    }

//    /**
//     * 리프레시토큰 업데이트
//     *
//     * @param updateRefreshToken
//     *     신규 리프레시토큰
//     */
//    public void updateRefreshToken(String updateRefreshToken) {
//        this.refreshToken = updateRefreshToken;
//    }

    /**
     * oAuth 리프레시토큰 업데이트
     *
     * @param oAuthRefreshToken
     *     신규 리프레시토큰
     */
    public void updateOauthRefreshToken(String oAuthRefreshToken) {
        this.oAuthRefreshToken = oAuthRefreshToken;
    }

    /**
     * oAuth 리프레시토큰 업데이트
     *
     * @param oAuthAccessToken
     *     신규 리프레시토큰
     */
    public void updateOauthAccessToken(String oAuthAccessToken) {
        this.oAuthAccessToken = oAuthAccessToken;
    }

    /**
     * 로그인 유지 여부 업데이트
     */
    public void updatePersistLogin(Boolean persistLogin) {
        this.persistLogin = persistLogin;
    }

    public void addAllUserAddress(Set<UserAddress> userAddresses) {
        this.userAddresses.addAll(userAddresses);
    }
    public void removeAllUserAddress(Set<UserAddress> userAddresses) {
        this.userAddresses.removeAll(userAddresses);
    }

    /**
     * oAuth 리프레시토큰 업데이트
     *
     * @param nickname
     *     신규 리프레시토큰
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }


    public void updateNickModDt(LocalDateTime nickExModDt) {
        this.nickLastModDt = nickExModDt;
    }

    public void updateGender(Gender gender) {
        this.gender = gender;
    }

    public void updateAge(AgeGroup age) {
        this.age = age.getAge();
    }

    public void updateSocialType(SocialType socialType) {
        this.socialType = socialType;
    }

}
