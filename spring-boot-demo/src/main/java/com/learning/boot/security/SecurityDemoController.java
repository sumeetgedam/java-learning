package com.learning.boot.security;


import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityDemoController {

    @GetMapping("/public/status")
    public String publicStatus() {
        return "Public";
    }

    @GetMapping("/profile")
    public String profile(
            Authentication authentication
    ) {
        return "Authenticated as : " +
                authentication.getName();
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "Admin dashboard";
    }


}
