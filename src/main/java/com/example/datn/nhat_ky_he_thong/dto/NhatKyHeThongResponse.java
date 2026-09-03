package com.example.datn.nhat_ky_he_thong.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NhatKyHeThongResponse {
    private Integer idNhatKy;
    private Integer idTaiKhoan;
    private String username;
    private String role;
    private String hanhDong;
    private String doiTuong;
    private Integer idDoiTuong;
    private String noiDung;
    private LocalDateTime thoiGian;
}