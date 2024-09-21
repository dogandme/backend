package com.mungwithme.marking.controller;

import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.marking.service.markingSaves.MarkingSavesService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


     /**
     *
     * 마킹을 저장(즐겨찾기)하는 API
     * @param markingId
     * @return
     * @throws IOException
     */
    @PostMapping("/{marking-id}")
    public ResponseEntity<CommonBaseResult> saveSaves(@PathVariable(name = "marking-id") Long markingId ) throws IOException {
        try {
            markingSavesService.addSaves(markingId);
            return baseResponse.sendSuccessResponse(HttpStatus.OK.value(),"ex) 저장 하셨습니다");
        } catch (IllegalArgumentException | ResourceNotFoundException e) {
            return baseResponse.sendErrorResponse(400, e.getMessage());
        }
    }

    /**
     * 저장을 취소하는 API

     * @param markingId

     * @return
     * @throws IOException
     */
    @DeleteMapping("/{marking-id}")
    public ResponseEntity<CommonBaseResult> removeSaves(@PathVariable(name = "marking-id") Long markingId) throws IOException {
        try {
            markingSavesService.deleteSaves(markingId);
            return baseResponse.sendSuccessResponse(HttpStatus.OK.value(),"ex) 저장에서 삭제했습니다");
        } catch (IllegalArgumentException | ResourceNotFoundException e) {
            return baseResponse.sendErrorResponse(400, e.getMessage());
        }
    }

}
