package com.mungwithme.user.controller;


import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.user.service.UserNotifyQueryDslService;
import com.mungwithme.user.service.UserNotifyService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/notify")
public class UserNotifyController {


    private final UserNotifyService userNotifyService;
    private final UserNotifyQueryDslService userNotifyQueryService;
    private final BaseResponse baseResponse;

    /**
     *
     * 유저 에게 온 알림 삭제
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonBaseResult> deleteNotify (@PathVariable(name = "id") Long id, HttpServletRequest request)
        throws IOException {
        userNotifyService.removeNotify(id);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(),"notify.remove.success",request.getLocale());
    }

    /**
     * 특정 알림 읽음 처리
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<CommonBaseResult> updateNotifyIsRead(@PathVariable(name = "id") Long id)
        throws IOException {
        userNotifyService.editIsRead(id,true);

        return baseResponse.sendSuccessResponse(HttpStatus.OK.value());
    }

    // 페이징 처리
    @GetMapping
    public ResponseEntity<CommonBaseResult> getNotifyList(@RequestParam(value = "offset",defaultValue = "0") int offset) {



        return null;
    }






}
