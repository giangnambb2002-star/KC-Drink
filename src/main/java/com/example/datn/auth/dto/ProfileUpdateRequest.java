package com.example.datn.auth.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class ProfileUpdateRequest {
    private String tenKhachHang; // Khớp với trường bên Frontend gửi lên
    private String email;
    private Boolean gioiTinh;
    private LocalDate ngaySinh;

    @Pattern(
            regexp = "^0\\d{9}$",
            message = "Số điện thoại phải bắt đầu bằng 0 và gồm đúng 10 chữ số"
    )
    private String sdt;
}