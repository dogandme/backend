package com.mungwithme.common.email;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/mail")
    public String mailSend(@RequestBody @Valid EmailRequestDto emailRequestDto) {
        log.info("인증 이메일 : {} ", emailRequestDto.getEmail());
        return mailService.joinEmail(emailRequestDto.getEmail());
    }

    @PostMapping("/mail/auth")
    public String mailAuth(@RequestBody @Valid EmailAuthRequestDto emailAuthRequestDto) {
        Boolean checked = mailService.authCheck(emailAuthRequestDto);

        // Todo checked 상태에 따라 분기 처리
        log.info("Email auth check : {}", checked);
        return "성공";
    }


}
