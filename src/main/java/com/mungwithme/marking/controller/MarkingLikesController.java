package com.mungwithme.marking.controller;

import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.likes.service.MarkingLikesService;
import jakarta.servlet.http.HttpServletRequest;
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


    private final MarkingLikesService markingLikesService;
    private final BaseResponse baseResponse;


    /**
     * 마킹에 좋아요를 추가하는 API
     *
     * @param contentId
     * @return
     * @throws IOException
     */
    @PostMapping("/{content-id}")
    public ResponseEntity<CommonBaseResult> createLikes(@PathVariable(name = "content-id") Long contentId,
        HttpServletRequest request)
        throws IOException {
        markingLikesService.addLikes(contentId);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(), "like.save.success", request.getLocale());
    }

    /**
     * 좋아요를 취소하는 API
     *
     * @param contentId
     * @return
     * @throws IOException
     */
    @DeleteMapping("/{content-id}")
    public ResponseEntity<CommonBaseResult> deleteLikes(@PathVariable(name = "content-id") Long contentId,
        HttpServletRequest request)
        throws IOException {
        markingLikesService.removeLikes(contentId);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(), "like.remove.success", request.getLocale());
    }

}
