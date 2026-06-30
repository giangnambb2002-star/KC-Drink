package com.example.datn.Config;

import com.example.datn.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // Auth
                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        // Tài khoản
                        .requestMatchers("/api/tai-khoan/**")
                        .hasRole("ADMIN")

                        // Nhân viên
                        .requestMatchers("/api/nhan-vien/**")
                        .hasRole("ADMIN")

                        // Khách hàng
                        .requestMatchers("/api/khach-hang/**")
                        .hasAnyRole("ADMIN" , "STAFF")
                        .requestMatchers("/api/dia-chi/**")
                        .hasAnyRole("ADMIN" ,"STAFF")
                        // Các API còn lại
                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}