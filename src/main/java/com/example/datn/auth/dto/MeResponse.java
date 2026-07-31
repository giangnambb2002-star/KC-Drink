package com.example.datn.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeResponse {

    private Integer idTaiKhoan;

    private String username;

    private String email;

    private String role;

    private String tenNguoiDung;

    private String chucVu;
    private String sdt;
    private Integer diemTichLuy;
    private Long soNgayDongHanh;
    private Integer idNhanVien;
    private Boolean gioiTinh;
    private LocalDate ngaySinh;
    private String tenNhanVien;

}