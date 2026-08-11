package com.example.datn.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePhoneRequest {

    @NotBlank(message = "Số điện thoại mới không được để trống")
    @Pattern(
            regexp = "^(0[0-9]{9})$",
            message = "Số điện thoại không đúng định dạng"
    )
    private String newPhone;

    @NotBlank(message = "Mật khẩu hiện tại không được để trống")
    private String password;
}