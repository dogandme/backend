package com.mungwithme.config;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 *
 * 각종 bean 을 관리하는 config class
 * 
 */
@RequiredArgsConstructor
@Configuration
public class SpringConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
