package com.mungwithme.marking.service;


import com.mungwithme.marking.model.dto.request.MarkingAddDto;
import com.mungwithme.marking.repository.MarkingRepository;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MarkingService {

    private final MarkingRepository markingRepository;
    private final UserService userService;



    /*
        저장

        수정

        삭제
     */


    /**
     *
     *
     *
     *
     * 유저가 작성한 마킹 정보 저장
     *
     *
     */
    public void addMarking(MarkingAddDto markingAddDto, List<MultipartFile> images) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);


        if (!StringUtils.hasText(markingAddDto.getContent()) || markingAddDto.getContent().length() > 150) {
            throw new IllegalArgumentException();
        }



    }

}
