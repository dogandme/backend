package com.mungwithme.common.email;

import com.mungwithme.common.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.activation.DataSource;
import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;

    @Value("${google.email.username}")
    private String username;

    private int authNumber;

    /**
     * 임의의 6자리 양수를 반환
     */
    public void makeRandomNumber() {
        Random r = new Random();
        String randomNumber = "";

        for (int i = 0; i < 7; i++) {
            randomNumber += Integer.toString(r.nextInt(10));
        }

        authNumber = Integer.parseInt(randomNumber);
    }

    /**
     * 회원가입 시 인증번호 전송
     * @param email 수신 email
     * @return
     */
    public void joinEmail(String email) {

        makeRandomNumber(); // 인증번호 생성

        String toMail = email;
        String title = "[멍윗미] 본인 인증 이메일 입니다.";
        String content = "<!DOCTYPE html>"
                + "<html lang='ko'>"
                + "<head>"
                + "    <meta charset='UTF-8'>"
                + "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "</head>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
                + "    <div style='max-width: 700px; margin: 50px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); padding: 30px;'>"
                + "        <div style='text-align: center; padding-bottom: 20px;'>"
                + "            <img src='cid:imageId' alt='멍윗미 로고' style='width: 100%; margin-bottom: 16px;'>"
                + "            <h1 style='font-size: 24px; color: #555;'>이메일 인증</h1>"
                + "        </div>"
                + "        <div style='text-align: center; line-height: 1.6; font-size: 18px; color: #333;'>"
                + "            <p>멍윗미를 방문해주셔서 감사합니다!</p>"
                + "            <p>아래 인증번호를 입력하여 이메일 인증을 완료해 주세요:</p>"
                + "            <div style='font-size: 32px; font-weight: bold; color: #ff6720; margin: 20px 0;'>" + authNumber + "</div>"
                + "            <p>이 인증번호는 3분간 유효합니다.</p>"
                + "        </div>"
                + "        <div style='text-align: center; padding-top: 20px; font-size: 14px; color: #888;'>"
                + "            <p>© 2024 멍윗미. All rights reserved.</p>"
                + "        </div>"
                + "    </div>"
                + "</body>"
                + "</html>";

        // 이미지 파일을 CID로 추가
        String imagePath = "/static/images/mail_top.png";
        log.info("imagePath : {}" ,imagePath);
        // redis에 인증번호 저장 (3분 유효)
        redisUtil.setDataExpire(Integer.toString(authNumber),toMail,3 * 60L);

        mailSend(username, toMail, title, content, imagePath);   // 메일 전송
    }

    /**
     * 이메일 발송
     * @param FROM_EMAIL 발신 email
     * @param toMail 수신 email
     * @param title 제목
     * @param content 내용
     */
    public void mailSend(String FROM_EMAIL, String toMail, String title, String content, String imagePath) {
        MimeMessage message = mailSender.createMimeMessage();//JavaMailSender 객체를 사용하여 MimeMessage 객체를 생성
        log.info("imagePath : {}" ,imagePath);
        try {
            ClassPathResource imgFile = new ClassPathResource(imagePath);
            log.info("imgFile : {}" ,imgFile);
            if (!imgFile.exists()) {
                throw new FileNotFoundException("Image file not found at " + imagePath);
            }

            InputStream inputStream = imgFile.getInputStream();
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

            DataSource dataSource = new ByteArrayDataSource(byteArrayOutputStream.toByteArray(), "image/png");
            System.out.println("dataSource : " + dataSource);
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");
            helper.setFrom(FROM_EMAIL);         //이메일의 발신자 주소 설정
            helper.setTo(toMail);               //이메일의 수신자 주소 설정
            helper.setSubject(title);           //이메일의 제목을 설정
            helper.setText(content,true);  //이메일의 내용 설정 두 번째 매개 변수에 true를 설정하여 html 설정으로한다.
            helper.addInline("imageId", dataSource);   // 이메일에 이미지 추가

            mailSender.send(message);// 이메일 전송

        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 인증번호 검증
     */
    public Boolean checkAuthNum(EmailAuthRequestDto emailAuthRequestDto) {

        String email = emailAuthRequestDto.getEmail();
        String authNum = emailAuthRequestDto.getAuthNum();

        if(redisUtil.getData(authNum)==null){
            return false;
        }
        else if(redisUtil.getData(authNum).equals(email)){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * 임시 비밀번호 전송
     * @param email 수신 이메일
     * @param temporaryPassword 임시 비밀번호
     */
    public void temporaryPasswordEmail(String email, String temporaryPassword) {
        String toMail = email;
        String title = "[멍윗미] 임시 비밀번호 이메일 입니다.";
        String content = "<!DOCTYPE html>"
                + "<html lang='ko'>"
                + "<head>"
                + "    <meta charset='UTF-8'>"
                + "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "</head>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
                + "    <div style='max-width: 700px; margin: 50px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); padding: 30px;'>"
                + "        <div style='text-align: center; padding-bottom: 20px;'>"
                + "            <img src='cid:imageId' alt='멍윗미 로고' style='width: 100%; margin-bottom: 16px;'>"
                + "            <h1 style='font-size: 24px; color: #555;'>임시 비밀번호</h1>"
                + "        </div>"
                + "        <div style='text-align: center; line-height: 1.6; font-size: 18px; color: #333;'>"
                + "            <p>멍윗미를 방문해주셔서 감사합니다!</p>"
                + "            <p>아래 임시 비밀번호를 입력하여 로그인을 하세요</p>"
                + "            <div style='font-size: 32px; font-weight: bold; color: #ff6720; margin: 20px 0;'>" + temporaryPassword + "</div>"
                + "        </div>"
                + "        <div style='text-align: center; padding-top: 20px; font-size: 14px; color: #888;'>"
                + "            <p>© 2024 멍윗미. All rights reserved.</p>"
                + "        </div>"
                + "    </div>"
                + "</body>"
                + "</html>";

        // 이미지 파일을 CID로 추가
        String imagePath = "src/main/resources/static/images/mail_top.png";

        mailSend(username, toMail, title, content, imagePath);   // 메일 전송
    }
}
