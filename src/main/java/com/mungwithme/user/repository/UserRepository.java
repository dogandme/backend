package com.mungwithme.user.repository;

import com.mungwithme.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * # OneToOne 양방향 매핑에서 연관관계의 주인이 아닌 쪽에서 조회하게 되면 프록시 객체를 생성할 수 없기 때문에 지연 로딩으로 설정해도 즉시 로딩으로 동작하게 된다.
     *
     * # 그 이유는 프록시는 null을 감쌀 수 없기 때문에 참조하고 있는 객체가 null인지 null이 아닌지 확인하는 쿼리를 실행해야 하기 때문이다.
     *
     *
     * @param email
     * @return
     */
    Optional<User> findByEmail(String email);

    Optional<User> findByRefreshToken(String refreshToken);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByEmailAndSocialTypeIsNull(String email);
}
