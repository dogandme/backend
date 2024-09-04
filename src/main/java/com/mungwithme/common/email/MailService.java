package com.mungwithme.common.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;
    private int authNumber;

    /**
     * 임의의 6자리 양수를 반환
     */
    public void makeRandomNumber() {
        Random r = new Random();
        String randomNumber = "";

        for (int i = 0; i < 6; i++) {
            randomNumber += Integer.toString(r.nextInt(10));
        }

        authNumber = Integer.parseInt(randomNumber);
    }

    /**
     * email 수신, 발신, 내용 설정
     * @param email 수신 email
     * @return
     */
    public String joinEmail(String email) {
        makeRandomNumber();

        String setFrom = "shjang0043@gmail.com";
        String toMail = email;
        String title = "[멍윗미] 본인 인증 이메일 입니다.";
        String content =
                "멍윗미를 방문해주셔서 감사합니다." +
                        "<br><br>" +
                        "인증 번호는 " + authNumber + "입니다." +
                        "<br>" +
                        "인증번호를 제대로 입력해주세요";

        mailSend(setFrom, toMail, title, content);
        return Integer.toString(authNumber);
    }

    /**
     * 이메일 발송
     * @param setFrom 발신 email
     * @param toMail 수신 email
     * @param title 제목
     * @param content 내용
     */
    public void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();//JavaMailSender 객체를 사용하여 MimeMessage 객체를 생성
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");//이메일 메시지와 관련된 설정을 수행합니다.
            // true를 전달하여 multipart 형식의 메시지를 지원하고, "utf-8"을 전달하여 문자 인코딩을 설정
            helper.setFrom(setFrom);//이메일의 발신자 주소 설정
            helper.setTo(toMail);//이메일의 수신자 주소 설정
            helper.setSubject(title);//이메일의 제목을 설정
            helper.setText(content,true);//이메일의 내용 설정 두 번째 매개 변수에 true를 설정하여 html 설정으로한다.

            mailSender.send(message);// 이메일 전송


        } catch (MessagingException e) {//이메일 서버에 연결할 수 없거나, 잘못된 이메일 주소를 사용하거나, 인증 오류가 발생하는 등 오류
            // 이러한 경우 MessagingException이 발생
            e.printStackTrace();//e.printStackTrace()는 예외를 기본 오류 스트림에 출력하는 메서드
        }
    }

    /**
     * 인증번호 검증
     */
    public Boolean authCheck(EmailAuthRequestDto emailAuthRequestDto) {
        return redisUtil.getData(String.valueOf(emailAuthRequestDto.getAuthNum().equals(emailAuthRequestDto.getEmail())))!=null?true:false;
    }
}
