package com.example.datn.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Tên đăng nhập hoặc số điện thoại không được để trống")
    @Pattern(
            regexp = "^\\S+$",
            message = "Tên đăng nhập hoặc số điện thoại không được chứa khoảng trắng"
    )
    private String usernameOrEmail;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}