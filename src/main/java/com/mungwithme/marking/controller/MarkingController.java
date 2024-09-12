package com.mungwithme.marking.controller;


import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.marking.model.dto.request.MarkingAddDto;
import com.mungwithme.marking.model.dto.request.MarkingModifyDto;
import com.mungwithme.marking.model.dto.request.MarkingRemoveDto;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.service.MarkingQueryService;
import com.mungwithme.marking.service.MarkingService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 마킹 CRUD controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/markings")
public class MarkingController {

    private final MarkingService markingService;
    private final BaseResponse baseResponse;
    private final MarkingQueryService markingQueryService;

    /**
     * marking 저장 API
     *
     * @param markingAddDto
     * @param images
     * @return
     */
    @PostMapping
    public CommonBaseResult saveMarkingWithImages(
        @Validated @RequestPart(name = "markingAddDto") MarkingAddDto markingAddDto,
        @RequestPart(name = "images") List<MultipartFile> images) {
        try {
            markingService.addMarking(markingAddDto, images, false);
        } catch (Exception e) {
            return baseResponse.getFailResult(400, e.getMessage());
        }
        return baseResponse.getSuccessResult();
    }

    /**
     * marking 수정 API
     *
     * @param markingModifyDto
     * @param images
     * @return
     */
    @PutMapping
    public CommonBaseResult modifyMarking(
        @Validated @RequestPart(name = "markingModifyDto") MarkingModifyDto markingModifyDto,
        @RequestPart(name = "images", required = false) List<MultipartFile> images) {
        try {
            // TODO postMan 테스트를 위해 임시
            if (images == null) {
                log.info("images = {}", images);
                images = new ArrayList<>();

            }
            markingService.patchMarking(markingModifyDto, images, false);
        } catch (Exception e) {
            return baseResponse.getFailResult(400, e.getMessage());
        }
        return baseResponse.getSuccessResult();
    }

    /**
     * marking 삭제 API
     */
    @DeleteMapping
    public CommonBaseResult removeMarking(@Validated @RequestBody MarkingRemoveDto markingRemoveDto) {
        try {
            markingService.deleteMarking(markingRemoveDto, false);
        } catch (Exception e) {
            return baseResponse.getFailResult(400, e.getMessage());
        }
        return baseResponse.getSuccessResult();
    }


    /**
     * 마킹 상세 정보 반환 API
     *
     * @param id
     *       마킹 아이디
     * @return
     */
    @GetMapping("/{id}")
    public CommonBaseResult fetchMarkingById(@PathVariable(name = "id") Long id) {
        try {
            MarkingInfoResponseDto markingInfoResponseDto = markingService.getMarkingInfoResponseDto(id, false, false);
            return baseResponse.getContentResult(markingInfoResponseDto);
        } catch (Exception e) {
            return baseResponse.getFailResult(400, e.getMessage());
        }
    }

}