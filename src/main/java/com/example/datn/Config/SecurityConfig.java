package com.example.datn.Config;
import com.example.datn.auth.security.CustomAccessDeniedHandler;
import com.example.datn.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exceptions ->
                        exceptions.accessDeniedHandler(customAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public
                        .requestMatchers("/error").permitAll()
                        // Auth
                        .requestMatchers(
                                "/api/auth/me",
                                "/api/auth/change-password",
                                "/api/auth/logout",
                                "/api/auth/update-profile"
                        ).authenticated()
                        .requestMatchers("/api/auth/**").permitAll()
                        // PayOS
                        .requestMatchers("/api/payos/create-payment").authenticated()
                        // ADMIN
                        .requestMatchers(
                                "/api/tai-khoan/**",
                                "/api/nhan-vien/**",
                                "/api/voucher/**"
                        ).hasRole("ADMIN")
                        // ADMIN + STAFF
                        .requestMatchers(
                                "/api/khach-hang/**",
                                "/api/dia-chi/**",
                                "/api/hoa-don/**"
                        ).hasAnyRole("ADMIN", "STAFF")
                        // Quản lý sản phẩm chỉ ADMIN
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/san-pham/manage",
                                "/api/san-pham/manage/**"
                        ).hasRole("ADMIN")
                        // STAFF được xem sản phẩm / size
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/san-pham/**",
                                "/api/size/**",
                                "/api/san-pham-size/**"
                        ).hasAnyRole("ADMIN", "STAFF")
                        // Chỉ ADMIN được thay đổi sản phẩm / size
                        .requestMatchers(
                                "/api/san-pham/**",
                                "/api/size/**",
                                "/api/san-pham-size/**"
                        ).hasRole("ADMIN")
                        // STAFF được xem bán thành phẩm / công thức BTP
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/ban-thanh-pham/**",
                                "/api/cong-thuc-san-pham-btp/**",
                                "/api/cong-thuc-ban-thanh-pham/**",
                                "/api/me-pha-che/**"
                        ).hasAnyRole("ADMIN", "STAFF")
                        // Chỉ ADMIN được thay đổi bán thành phẩm
                        .requestMatchers(
                                "/api/ban-thanh-pham/**",
                                "/api/cong-thuc-san-pham-btp/**",
                                "/api/cong-thuc-ban-thanh-pham/**",
                                "/api/me-pha-che/**"
                        ).hasRole("ADMIN")
                        // STAFF được xem công thức sản phẩm
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/cong-thuc/**"
                        ).hasAnyRole("ADMIN", "STAFF")
                        // Chỉ ADMIN được thay đổi công thức sản phẩm
                        .requestMatchers(
                                "/api/cong-thuc/**"
                        ).hasRole("ADMIN")
                        // STAFF được xem nguyên liệu / topping
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/nguyen-lieu/**",
                                "/api/lo-nguyen-lieu/**",
                                "/api/topping/**",
                                "/api/lo-topping/**"
                        ).hasAnyRole("ADMIN", "STAFF")
                        // Chỉ ADMIN được thay đổi nguyên liệu / topping
                        .requestMatchers(
                                "/api/nguyen-lieu/**",
                                "/api/lo-nguyen-lieu/**",
                                "/api/topping/**",
                                "/api/lo-topping/**",
                                "/api/lo-topping/import"
                        ).hasRole("ADMIN")
                        .requestMatchers("/api/nhat-ky-he-thong/**").hasRole("ADMIN")
                        .requestMatchers("/api/dashboard/**").hasRole("ADMIN")
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