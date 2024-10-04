package com.mungwithme.user.service;


import com.mungwithme.address.model.dto.response.AddressResponseDto;
import com.mungwithme.common.exception.DuplicateResourceException;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.user.model.enums.Role;
import com.mungwithme.user.model.dto.response.UserMyInfoResponseDto;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.repository.UserQueryRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * user select 문을 모아 놓은 service
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {


    private final UserQueryRepository userQueryRepository;

    /**
     * SecurityContextHolder > UserDetails에서 User 조회
     * 비회원이라도 예외처리를 발생 시키지 않고 null 값을 반환한다.
     *
     * 기존 findCurrentUser 는 unCheckedException 를 발생시켰는데
     * try-catch 문으로 예외처리를 하더라도 rollback 되버리는 현상이 발생한다
     * Transactional(readOnly = true) 해도 마찬가지이다.
     * 그리고 데이터를 받을 수 없게 된다
     *
     * 비회원이 접근 할 수 있는 API 에서는 null 값으로 처리 하여서
     * 회원인지 비회원인지 구분하자
     *
     * @return
     */
    public User findCurrentUser_v2() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority
        ).findFirst().orElse(null);

        if (role == null || role.equals(Role.ANONYMOUS.getAuthority())) {
            return null;
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        return findByEmail(email).orElse(null);
    }


    /**
     * SecurityContextHolder > UserDetails에서 User 조회
     * @return
     */
    public User findCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principal = authentication.getPrincipal();
        String email = null;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        if (email != null) {
            return userQueryRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("error.notfound.user"));
        } else {
            throw new ResourceNotFoundException("error.notfound.user");
        }
    }


    /**
     * 이메일을 이용하여 회원 조회
     *
     * @param id id
     * @return 조회된 회원
     */
    public Optional<User> findById(long id) {
        return userQueryRepository.findById(id);
    }

    /**
     * 이메일을 이용하여 회원 조회
     *
     * @param email 이메일
     * @return 조회된 회원
     */
    public Optional<User> findByEmail(String email) {
        return userQueryRepository.findByEmail(email);
    }




    /**
     * 이메일을 이용하여 일반 회원 조회
     * @param email 이메일
     * @return 조회된 회원
     */
    public Optional<User> findByEmailAndSocialTypeIsNull(String email) {
        return userQueryRepository.findByEmailAndSocialTypeIsNull(email);
    }

    /**
     * 닉네임을 이용하여 회원 조회
     * @param nickname 닉네임
     * @return 조회된 회원
     */
    public Optional<User> findByNickname(String nickname) {
        return userQueryRepository.findByNickname(nickname);
    }


    /**
     * 닉네임 중복 확인
     *
     * @param nickname
     */
    public void duplicateNickname(String nickname) {
        userQueryRepository.findByNickname(nickname)
            .ifPresent(user -> {
                throw new DuplicateResourceException("error.duplicate.nickname");
            });
    }


    /**
     * 이메일을 이용하여 회원 조회
     *
     * @param email 이메일
     * @return 조회된 회원
     */
    public Optional<User> findByEmailWithAddress(String email) {
        return userQueryRepository.findByEmailWithAddress(email);
    }


    public UserMyInfoResponseDto findMyInfo () {
        User currentUser = findCurrentUser();

        List<AddressResponseDto> regions = currentUser.getRegions().stream()
            .map(region -> new AddressResponseDto(region.getId(), region.getProvince(),
                region.getCityCounty(), region.getDistrict(),
                region.getSubDistrict())).toList();
        return UserMyInfoResponseDto.builder().nickname(currentUser.getNickname()).age(currentUser.getAge())
            .gender(currentUser.getGender()).regions(regions).build();
    }

    /**
     * email 중복 확인
     * @param email
     */
    public void duplicateEmail(String email) {
        userQueryRepository.findByEmail(email)
            .ifPresent(user -> {
                throw new DuplicateResourceException("error.duplicate.email");
            });
    }


}
