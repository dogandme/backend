package com.mungwithme.user.model.entity;

import com.mungwithme.user.model.Role;
import com.mungwithme.user.model.SocialType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 회원 ENTITY
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")   // 컬럼명 : user_iD Todo 확인필요
    private Long id;

    // @Column 어노테이션을 명시적으로 지정하지 않아도, JPA는 엔티티 클래스의 필드를 기본적으로 데이터베이스 테이블의 컬럼으로 매핑합니다. Todo 확인필요
    private String email;           // 이메일(ID)
    private String password;        // 비밀번호
    private String nickname;        // 닉네임
    private String imageUrl;        // 프로필 이미지
    private int age;                // 나이(추가정보)
    private String city;            // 사는 도시(추가정보)

    @Enumerated(EnumType.STRING)
    private Role role;              // 권한 (기본:GUEST, 선택정보입력시:USER, 관리자:ADMIN)

    @Enumerated(EnumType.STRING)
    private SocialType socialType;  // 소셜 채널(Kakao, Google, Naver)

    private String socialId;        // 로그인한 소셜 타입의 식별자 값(일반 로그인:null)
    private String refreshToken;

    /**
     * 유저 권한 설정 메소드
     */
    public void authorizeUser() {
        this.role = Role.USER;
    }

    /**
     * 비밀번호 암호화 메소드
     * @param passwordEncoder 비밀번호 인코더
     */
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    /**
     * 리프레시토큰 업데이트
     * @param updateRefreshToken 신규 리프레시토큰
     */
    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }
}
