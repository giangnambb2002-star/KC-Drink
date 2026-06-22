package com.example.datn.nhan_vien.dto;

import lombok.Data;

@Data
public class NhanVienResponse {

    private Integer idNhanVien;

    private String tenNhanVien;

    private String sdt;

    private String email;

    private String chucVu;

    private Integer trangThai;

    private Integer idTaiKhoan;

    private String username;
}