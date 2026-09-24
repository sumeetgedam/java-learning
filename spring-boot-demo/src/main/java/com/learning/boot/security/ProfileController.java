package com.learning.boot.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ProfileController {

    @GetMapping("/public/status/user")
    public Map<String, String> publicStatus() {
        return Map.of(
                "status",
                "ok"
        );
    }

    @GetMapping("/profile/user")
    public Map<String, String> profile(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return Map.of(
                "subject",
                jwt.getSubject(),
                "issuer",
                jwt.getIssuer().toString()
        );
    }
}
