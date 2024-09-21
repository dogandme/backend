package com.mungwithme.marking.controller;

import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.likes.model.enums.ContentType;
import com.mungwithme.likes.service.LikesService;
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
@RequestMapping("/markings/likes")
public class MarkingLikesController {


    private final LikesService likesService;
    private final BaseResponse baseResponse;


    /**
     * 마킹에 좋아요를 추가하는 API
     * @param contentId
     * @return
     * @throws IOException
     */
    @PostMapping("/{content-id}")
    public ResponseEntity<CommonBaseResult> saveLikes(@PathVariable(name = "content-id") Long contentId ) throws IOException {
        try {
            likesService.addLikes(contentId, ContentType.MARKING);
            return baseResponse.sendSuccessResponse(HttpStatus.OK.value(),"ex) 좋아요 추가 하셨습니다");
        } catch (IllegalArgumentException | ResourceNotFoundException e) {
            return baseResponse.sendErrorResponse(400, e.getMessage());
        }
    }

    /**
     * 좋아요를 취소하는 API
     * @param contentId
     * @return
     * @throws IOException
     */
    @DeleteMapping("/{content-id}")
    public ResponseEntity<CommonBaseResult> removeLikes(@PathVariable(name = "content-id") Long contentId ) throws IOException {
        try {
            likesService.deleteLikes(contentId, ContentType.MARKING);
            return baseResponse.sendSuccessResponse(HttpStatus.OK.value(),"ex) 좋아요 취소 하셨습니다");
        } catch (IllegalArgumentException | ResourceNotFoundException e) {
            return baseResponse.sendErrorResponse(400, e.getMessage());
        }
    }

}
