package com.example.datn.auth.controller;

import com.example.datn.auth.dto.ChangePhoneRequest;
import com.example.datn.auth.dto.CustomerProfileUpdateRequest;
import com.example.datn.auth.dto.LoginRequest;
import com.example.datn.auth.dto.RegisterRequest;
import com.example.datn.auth.service.AuthService;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import com.example.datn.common.ApiResponse;
import com.example.datn.auth.dto.ForgotPasswordRequest;
import com.example.datn.auth.dto.ProfileUpdateRequest;
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
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Mật khẩu mới đã được gửi đến email của bạn",
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
    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @Valid @RequestBody ProfileUpdateRequest request
    ) {
        if (authentication == null) {
            throw new RuntimeException("Chưa đăng nhập");
        }

        TaiKhoan taiKhoan = (TaiKhoan) authentication.getPrincipal();
        authService.updateProfile(taiKhoan, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Cập nhật thông tin cá nhân thành công",
                        null
                )
        );
    }
    @PutMapping("/update-customer-profile")
    public ResponseEntity<?> updateCustomerProfile(
            Authentication authentication,
            @Valid @RequestBody CustomerProfileUpdateRequest request
    ) {
        if (authentication == null) {
            throw new RuntimeException("Chưa đăng nhập");
        }

        TaiKhoan taiKhoan = (TaiKhoan) authentication.getPrincipal();
        authService.updateCustomerProfile(taiKhoan, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Cập nhật thông tin khách hàng thành công",
                        null
                )
        );
    }
    @PostMapping("/change-phone")
    public ResponseEntity<?> changePhone(
            Authentication authentication,
            @Valid @RequestBody ChangePhoneRequest request
    ) {
        if (authentication == null) {
            throw new RuntimeException("Chưa đăng nhập");
        }

        TaiKhoan taiKhoan = (TaiKhoan) authentication.getPrincipal();
        authService.changePhone(taiKhoan, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Đổi số điện thoại đăng nhập thành công",
                        null
                )
        );
    }
}
