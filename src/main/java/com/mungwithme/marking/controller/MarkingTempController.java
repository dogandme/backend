package com.mungwithme.marking.controller;


import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.marking.model.dto.request.MarkingAddDto;
import com.mungwithme.marking.model.dto.request.MarkingModifyDto;
import com.mungwithme.marking.model.dto.request.MarkingRemoveDto;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.service.marking.MarkingTempService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<CommonBaseResult> saveTempMarkingWithImages(
        @Validated @RequestPart(name = "markingAddDto") MarkingAddDto markingAddDto,
        @RequestPart(name = "images", required = false) List<MultipartFile> images) throws IOException {
        try {
            markingTempService.addTempMarking(markingAddDto, images);
            return baseResponse.sendSuccessResponse(200, "ex) 마킹 임시 저장에 성공하셨습니다.");
        } catch (Exception e) {
            return baseResponse.sendErrorResponse(400, e.getMessage());
        }
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
    public ResponseEntity<CommonBaseResult> modifyTempMarking(
        @Validated @RequestPart(name = "markingModifyDto") MarkingModifyDto markingModifyDto,
        @RequestPart(name = "images", required = false) List<MultipartFile> images) throws IOException {
        try {
            // TODO postMan 테스트를 위해 임시
            if (images == null) {
                images = new ArrayList<>();
            }
            markingTempService.patchTempMarking(markingModifyDto, images);

            String code = "ex) 최종 저장 하셨습니다";
            if (markingModifyDto.getIsTempSaved()) {
                code = "ex) 수정 완료 되었습니다";
            }

            return baseResponse.sendSuccessResponse(200, code);
        } catch (Exception e) {
            return baseResponse.sendErrorResponse(400, e.getMessage());
        }
    }

    /**
     * 임시저장 marking 삭제 API
     */
    @DeleteMapping
    public ResponseEntity<CommonBaseResult> removeTempMarking(@Validated @RequestBody MarkingRemoveDto markingRemoveDto)
        throws IOException {
        try {
            markingTempService.deleteTempMarking(markingRemoveDto);

            return baseResponse.sendSuccessResponse(200, "ex) 삭제 되었습니다.");
        }  catch (Exception e) {
            return baseResponse.sendErrorResponse(400, e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<CommonBaseResult> fetchTempMarkingById(@PathVariable(name = "id") Long id)
        throws IOException {
        try {
            MarkingInfoResponseDto tempMarkingInfoResponseDto = markingTempService.getTempMarkingInfoResponseDto(id,
                false,
                true);
            return baseResponse.sendContentResponse(tempMarkingInfoResponseDto,200);
        } catch (Exception e) {
            return baseResponse.sendErrorResponse(400, e.getMessage());
        }
    }


}
