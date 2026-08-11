package com.example.datn.auth.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class CustomerProfileUpdateRequest {
    private String tenKhachHang;
    private String email;
    private String sdt;
    private Boolean gioiTinh;
    private LocalDate ngaySinh;
}