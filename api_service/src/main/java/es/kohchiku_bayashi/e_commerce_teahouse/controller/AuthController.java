package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
            // OAuth2 authorization code
            return ResponseEntity.ok().body(Collections.singletonMap("code", code));
        }
        
        return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Missing token or code"));
    }

    @PostMapping("/api/auth/token")
    public ResponseEntity<Object> exchangeAuthorizationCode(@RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Missing code"));
        }

        // Llamada al Authorization Server para intercambiar code por token
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("client-app", "1234");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", "http://localhost:4200/authorized");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Object> response = restTemplate.exchange(
            "http://localhost:9000/oauth2/token",
            HttpMethod.POST,
            request,
            Object.class
        );

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}