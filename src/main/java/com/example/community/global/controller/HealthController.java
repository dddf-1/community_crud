package com.example.community.global.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 서버 상태 확인용 컨트롤러
 * 배포 후 EC2에서 백엔드 서버가 정상적으로 실행 중인지 확인하기 위해 사용한다.
 */
@RestController
public class HealthController {

    /**
     * GET /health
     * 서버가 정상적으로 실행 중이면 200 OK와 "OK"를 반환한다.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}