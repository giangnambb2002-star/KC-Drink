package com.example.datn.Config;

import com.example.datn.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
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
                // Tích hợp an toàn: Lệnh này báo cho Spring Security tự động
                // tìm và sử dụng cấu hình CORS bên file CorsConfig.java của team bạn
                .cors(Customizer.withDefaults())

                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // Mở cửa cho đường dẫn xuất thông báo lỗi ngầm định của Spring
                        .requestMatchers("/error").permitAll()

                        // Những API bắt buộc phải có Token
                        .requestMatchers("/api/auth/me", "/api/auth/change-password", "/api/auth/logout").authenticated()

                        // Những API Auth mở tự do (login, register, forgot-password)
                        .requestMatchers("/api/auth/**").permitAll()

                        // Phân quyền Quản trị & Nhân viên
                        .requestMatchers("/api/tai-khoan/**", "/api/nhan-vien/**" ,"/api/voucher/**").hasRole("ADMIN")
                        .requestMatchers("/api/khach-hang/**", "/api/dia-chi/**").hasAnyRole("ADMIN" , "STAFF")

                        // Khóa chặt các API còn lại
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

}