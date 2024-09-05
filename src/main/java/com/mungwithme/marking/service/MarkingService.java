package com.mungwithme.marking.service;


import com.mungwithme.marking.repository.MarkingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MarkingService {

    private final MarkingRepository markingRepository;


    /*
        저장

        수정

        삭제
     */


    /**
     *
     * 유저가 작성한 마킹 정보 저장
     *
     *
     */
    public void addMarking() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();




    }

}
