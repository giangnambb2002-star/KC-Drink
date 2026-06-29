package com.example.datn.khach_hang.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class KhachHangResponse {

    private Integer idKhachHang;
    private String tenKhachHang;
    private String sdt;
    private String email;
    private String diaChi;
    private Boolean gioiTinh;
    private LocalDate ngaySinh;
    private Integer diemTichLuy;
    private Integer trangThai;

    private Integer idTaiKhoan;
    private String username;
}