package com.example.datn.auth.dto;

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
}