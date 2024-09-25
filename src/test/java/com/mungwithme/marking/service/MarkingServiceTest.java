package com.mungwithme.marking.service;

import com.mungwithme.marking.model.enums.Visibility;
import com.mungwithme.marking.model.dto.request.MarkingAddDto;
import com.mungwithme.marking.model.dto.request.MarkingModifyDto;
import com.mungwithme.marking.model.dto.request.MarkingRemoveDto;
import com.mungwithme.marking.repository.markImge.MarkImageRepository;
import com.mungwithme.marking.repository.marking.MarkingQueryRepository;
import com.mungwithme.marking.service.marking.MarkingService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
class MarkingServiceTest {


    @Autowired
    MarkingService markingService;

    @Autowired
    MarkImageRepository markImageRepository;

    @Autowired
    MarkingQueryRepository markingQueryRepository;

    @Test
    void addMarking() throws IOException {

        // SecurityContextHolder.getContext()로 SecurityContext를 꺼낸 후,
        // setAuthentication()을 이용하여 위에서 만든 Authentication 객체에 대한 인증 허가 처리
//        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<MultipartFile> images = new ArrayList<>();

        // 실제 파일을 MultipartFile로 변환 ;
        MultipartFile multipartFile1 = getMultipartFile();

        MultipartFile multipartFile2 = getMultipartFile();
        MultipartFile multipartFile3 = getMultipartFile();
        MultipartFile multipartFile4 = getMultipartFile();

        images.add(multipartFile1);
        images.add(multipartFile2);
        images.add(multipartFile3);
        images.add(multipartFile4);

        MarkingAddDto markingAddDto = new MarkingAddDto();

        markingAddDto.setContent("안녕하시와요");
        markingAddDto.setLat(36.55);
        markingAddDto.setLng(126.55);
        markingAddDto.setIsVisible(Visibility.FOLLOWERS_ONLY);
        markingAddDto.setRegion("하하요하요");
        markingService.addMarking(markingAddDto, images, false);

    }

    private static MultipartFile getMultipartFile() throws IOException {
        File file = new File("/Users/imhaneul/Downloads/뉴진스/NJ_BubbleGum_21.jpg");
        FileInputStream inputStream = new FileInputStream(file);
        // 실제 파일을 MultipartFile로 변환
        return new MockMultipartFile(
            "file",
            file.getName(),
            "image/jpeg",
            inputStream
        );
    }


    @Test
    public void deleteMarking() {

        MarkingRemoveDto markingRemoveDto = new MarkingRemoveDto();

        markingRemoveDto.setId(10L);
        markingService.removeMarking(markingRemoveDto,false);
    }


    @Test
    public void patchMarking() throws IOException {
        // given
        List<MultipartFile> images = new ArrayList<>();
        MultipartFile multipartFile1 = getMultipartFile();
        MultipartFile multipartFile2 = getMultipartFile();

        images.add(multipartFile1);
        images.add(multipartFile2);

        MarkingModifyDto markingModifyDto = new MarkingModifyDto();

        Set<Long> removeIds = new HashSet<>();

        removeIds.add(38L);
        removeIds.add(37L);

        markingModifyDto.setRemoveIds(removeIds);
        markingModifyDto.setContent("변경했어요");
        markingModifyDto.setIsVisible(Visibility.FOLLOWERS_ONLY);

        markingModifyDto.setId(15L);

        // when
        markingService.editMarking(markingModifyDto, images,false);

        // then

    }

}