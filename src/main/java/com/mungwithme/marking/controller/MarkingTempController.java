package com.mungwithme.marking.controller;


import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.marking.model.dto.request.MarkingAddDto;
import com.mungwithme.marking.model.dto.request.MarkingModifyDto;
import com.mungwithme.marking.model.dto.request.MarkingRemoveDto;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.model.dto.response.TempMarkingInfoResponseDto;
import com.mungwithme.marking.service.MarkingTempService;
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
 * 마킹 임시 저장 CRUD controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/markings/temp")
public class MarkingTempController {

    private final MarkingTempService markingTempService;
    private final BaseResponse baseResponse;


    /**
     * marking 임시 저장 API
     *
     * @param markingAddDto
     * @param images
     * @return
     */
    @PostMapping
    public CommonBaseResult saveTempMarkingWithImages(
        @Validated @RequestPart(name = "markingAddDto") MarkingAddDto markingAddDto,
        @RequestPart(name = "images", required = false) List<MultipartFile> images) {
        try {
            markingTempService.addTempMarking(markingAddDto, images);
        } catch (Exception e) {
            return baseResponse.getFailResult(400, e.getMessage());
        }
        return baseResponse.getSuccessResult();
    }


    /**
     * 임시 저장 marking 수정 API
     *
     *
     *
     * @param markingModifyDto
     * @param images
     * @return
     */
    @PutMapping
    public CommonBaseResult modifyTempMarking(
        @Validated @RequestPart(name = "markingModifyDto") MarkingModifyDto markingModifyDto,
        @RequestPart(name = "images", required = false) List<MultipartFile> images) {
        try {
            // TODO postMan 테스트를 위해 임시
            if (images == null) {
                images = new ArrayList<>();
            }
            markingTempService.patchTempMarking(markingModifyDto, images);
        } catch (Exception e) {
            return baseResponse.getFailResult(400, e.getMessage());
        }
        return baseResponse.getSuccessResult();
    }

    /**
     * 임시저장 marking 삭제 API
     */
    @DeleteMapping
    public CommonBaseResult removeTempMarking(@Validated @RequestBody MarkingRemoveDto markingRemoveDto) {
        try {
            markingTempService.deleteTempMarking(markingRemoveDto);
        } catch (Exception e) {
            return baseResponse.getFailResult(400, e.getMessage());
        }
        return baseResponse.getSuccessResult();
    }


    @GetMapping("/{id}")
    public CommonBaseResult fetchTempMarkingById(@PathVariable(name = "id") Long id) {
        try {
            MarkingInfoResponseDto tempMarkingInfoResponseDto = markingTempService.getTempMarkingInfoResponseDto(id,
                false,
                true);
            return baseResponse.getContentResult(tempMarkingInfoResponseDto);
        } catch (Exception e) {
            return baseResponse.getFailResult(400, e.getMessage());
        }
    }


}
