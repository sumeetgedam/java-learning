package com.learning.boot.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder
    ) {
        UserDetails user =
                User.builder()
                        .username("alex")
                        .password(
                                passwordEncoder.encode(
                                        "secret"
                                )
                        )
                        .roles("USER")
                        .build();

        UserDetails admin = User
                .builder().username("admin")
                .password(
                        passwordEncoder.encode(
                                "admin-secret"
                        )
                )
                .roles("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(
                user,
                admin
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity
    ) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests( authorize ->
                        authorize.requestMatchers(
                                "/public/**"
                                ).permitAll()
                                .requestMatchers(
                                        "/users/**"
                                )
                                .hasAuthority("SCOPE_users.read")
                                .anyRequest()
                                .authenticated()
        )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> {

                        })
                );

        return httpSecurity.build();
    }
}

