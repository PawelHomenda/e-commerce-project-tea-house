package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @GetMapping("/authorized")
    public ResponseEntity<Map<String, String>> getAuth(
    		@RequestParam(required = false) String code,
    		@RequestParam(required = false) String token) {
        
        if (token != null && !token.isEmpty()) {
            // Token JWT received from auth_server
            return ResponseEntity.ok().body(Collections.singletonMap("token", token));
        } else if (code != null && !code.isEmpty()) {
            // OAuth2 code (legacy)
            return ResponseEntity.ok().body(Collections.singletonMap("code", code));
        }
        
        return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Missing token or code"));
    }
}

