package com.group8.alomilktea.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${API_PREFIX}/logs")
@RequiredArgsConstructor
public class LogTestController {

    private static final Logger log = LoggerFactory.getLogger(LogTestController.class);

    @GetMapping("/test")
    public ResponseEntity<String> testLogs() {
        log.info("Test log - user accessed log test endpoint");

        try {
            throw new IllegalStateException("Fake security error for testing");
        } catch (Exception e) {
            log.error("Security event: error occurred during log test: {}", e.getMessage());
        }

        return ResponseEntity.ok("Check log in logs/security.log");
    }
}


