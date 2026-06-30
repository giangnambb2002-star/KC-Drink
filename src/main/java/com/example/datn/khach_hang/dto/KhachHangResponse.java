package com.example.datn.khach_hang.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Data
public class KhachHangResponse {

    private Integer idKhachHang;
    private String tenKhachHang;
    private String sdt;
    private String email;
    private String diaChiMacDinh;
    private Boolean gioiTinh;
    private LocalDate ngaySinh;
    private Integer diemTichLuy;
    private Integer trangThai;
    private Integer idTaiKhoan;
    private String username;
}