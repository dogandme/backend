package com.mungwithme.common.healthCheck;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthCheck")
public class HealthCheckController {
    @GetMapping("")
    public ResponseEntity<String> awsHealthCheck() {
        return ResponseEntity.ok("AWS Health Check Successful");
    }
}
