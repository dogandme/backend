package com.mungwithme.marking.controller;


import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.marking.model.dto.request.MarkingAddDto;
import com.mungwithme.marking.service.MarkingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/markings")
public class MarkingController {


    private final MarkingService markingService;
    private final BaseResponse baseResponse;


    /**
     * marking 저장 API
     *
     * @param markingAddDto
     * @param images
     * @return
     */
    @PostMapping("")
    public CommonBaseResult saveMarkingWithImages(@RequestPart MarkingAddDto markingAddDto,
        @RequestPart List<MultipartFile> images) {

        return null;
    }


}
