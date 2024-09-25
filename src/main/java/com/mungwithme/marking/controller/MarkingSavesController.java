package com.mungwithme.marking.controller;

import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.marking.service.markingSaves.MarkingSavesService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/markings/saves")
public class MarkingSavesController {


    private final MarkingSavesService markingSavesService;
    private final BaseResponse baseResponse;
    private final MessageSource ms;


    /**
     * 마킹을 저장(즐겨찾기)하는 API
     *
     * @param markingId
     * @return
     * @throws IOException
     */
    @PostMapping("/{marking-id}")
    public ResponseEntity<CommonBaseResult> saveSaves(@PathVariable(name = "marking-id") Long markingId,
        HttpServletRequest request)
        throws IOException {
        markingSavesService.addSaves(markingId);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(),
            ms.getMessage("saves.save.success", null, request.getLocale()));

    }

    /**
     * 저장을 취소하는 API
     *
     * @param markingId
     * @return
     * @throws IOException
     */
    @DeleteMapping("/{marking-id}")
    public ResponseEntity<CommonBaseResult> removeSaves(@PathVariable(name = "marking-id") Long markingId,
        HttpServletRequest request)
        throws IOException {
        markingSavesService.deleteSaves(markingId);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(),
            ms.getMessage("saves.remove.success", null, request.getLocale()));

    }

}
