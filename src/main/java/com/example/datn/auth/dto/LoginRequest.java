package com.example.datn.auth.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Username hoặc email không được để trống")
    private String usernameOrEmail;

    @NotBlank(message = "Password không được để trống")
    private String password;
}
