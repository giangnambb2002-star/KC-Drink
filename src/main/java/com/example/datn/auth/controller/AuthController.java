package com.example.datn.auth.controller;

import com.example.datn.auth.dto.LoginRequest;
import com.example.datn.auth.dto.RegisterRequest;
import com.example.datn.auth.service.AuthService;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import com.example.datn.common.ApiResponse;
import com.example.datn.auth.dto.ChangePasswordRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;



    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        authService.register(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Đăng ký thành công",
                        null
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request
    ) {
        String token = authService.login(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Đăng nhập thành công",
                        token
                )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(
            Authentication authentication
    ) { if (authentication == null) { throw new RuntimeException(
                    "Chưa đăng nhập"); }
        TaiKhoan taiKhoan = (TaiKhoan) authentication.getPrincipal();
        return ResponseEntity.ok(
                new ApiResponse<>
                        (200, "Lấy thông tin thành công", authService.getCurrentUser(taiKhoan)));
    }
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        TaiKhoan taiKhoan = (TaiKhoan) authentication.getPrincipal();authService.changePassword(taiKhoan, request);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Đổi mật khẩu thành công",
                        null
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Đăng xuất thành công",
                        null
                )
        );
    }
}
