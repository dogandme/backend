package com.mungwithme.marking.controller;


import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.marking.model.dto.request.MarkingAddDto;
import com.mungwithme.marking.model.dto.request.MarkingModifyDto;
import com.mungwithme.marking.model.dto.request.MarkingRemoveDto;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.service.marking.MarkingTempService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CommonBaseResult> createTempMarkingWithImages(
        @Validated @RequestPart(name = "markingAddDto") MarkingAddDto markingAddDto,
        @RequestPart(name = "images", required = false) List<MultipartFile> images, HttpServletRequest request)
        throws IOException {
        markingTempService.addTempMarking(markingAddDto, images);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(), "marking.temp.save.success",
            request.getLocale());

    }


    /**
     * 임시 저장 marking 수정 API
     *
     * @param markingModifyDto
     * @param images
     * @return
     */
    @PutMapping
    public ResponseEntity<CommonBaseResult> updateTempMarking(
        @Validated @RequestPart(name = "markingModifyDto") MarkingModifyDto markingModifyDto,
        @RequestPart(name = "images", required = false) List<MultipartFile> images, HttpServletRequest request)
        throws IOException {
        // TODO postMan 테스트를 위해 임시
        if (images == null) {
            images = new ArrayList<>();
        }
        markingTempService.editTempMarking(markingModifyDto, images);

        String code = "marking.save.success";
        if (markingModifyDto.getIsTempSaved()) {
            code = "modify.success";
        }

        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(), code, request.getLocale());

    }

    /**
     * 임시저장 marking 삭제 API
     */
    @DeleteMapping
    public ResponseEntity<CommonBaseResult> deleteTempMarking(@Validated @RequestBody MarkingRemoveDto markingRemoveDto,
        HttpServletRequest request)
        throws IOException {
        markingTempService.removeTempMarking(markingRemoveDto);

        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(), "marking.remove.success", request.getLocale());

    }


    @GetMapping("/{id}")
    public ResponseEntity<CommonBaseResult> fetchTempMarkingById(@PathVariable(name = "id") Long id)
        throws IOException {
        MarkingInfoResponseDto tempMarkingInfoResponseDto = markingTempService.findTempMarkingInfoResponseDto(id,
            false,
            true);
        return baseResponse.sendContentResponse(tempMarkingInfoResponseDto, HttpStatus.OK.value());

    }


}
