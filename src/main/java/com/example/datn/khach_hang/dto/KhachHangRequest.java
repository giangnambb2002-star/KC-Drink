package com.example.datn.khach_hang.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class KhachHangRequest {

    private String tenKhachHang;

    private String sdt;

    private String email;

    private String diaChi;

    private Boolean gioiTinh;

    private LocalDate ngaySinh;

    private Integer diemTichLuy;

    private Integer idTaiKhoan;
}