package com.mungwithme.common.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Bean
    public JavaMailSender mailSender() {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("shjang0043@gmail.com");
        mailSender.setPassword("dtop uubv ezrl lrgf");

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.transport.protocol", "smtp");  // smtp 프로토콜 사용
        javaMailProperties.put("mail.smtp.auth", "true");           // smtp 서버에 인증이 필요
        javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");  // SSL 소켓 팩토리 클래스 사용
        javaMailProperties.put("mail.debug", "true");               // 디버깅 정보 출력
        javaMailProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        javaMailProperties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        javaMailProperties.put("mail.smtp.starttls.enable", "true");    // STARTTLS 활성화
        javaMailProperties.put("mail.smtp.starttls.required", "true");  // STARTTLS 필수로 설정

        mailSender.setJavaMailProperties(javaMailProperties);

        return mailSender;
    }
}
