package com.travels.backend.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meta")
public class MetaController {

    @GetMapping("/server-date")
    public ResponseEntity<Map<String, String>> serverDate() {
        return ResponseEntity.ok(Map.of("today", LocalDate.now().toString()));
    }
}
