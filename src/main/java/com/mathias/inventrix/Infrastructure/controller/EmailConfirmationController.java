package com.mathias.inventrix.Infrastructure.controller;

import com.mathias.inventrix.service.TokenValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class EmailConfirmationController {
    private final TokenValidationService tokenValidationService;

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmEmail(@RequestParam("token") String token){

        String result = tokenValidationService.validateToken(token);
        if ("Email confirmed successfully".equals(result)) {
            return ResponseEntity.ok(Collections.singletonMap("message", result));
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", result));
        }

    }

}
