package com.example.datn.nhan_vien.dto;

import lombok.Data;

@Data
public class NhanVienRequest {

    private String tenNhanVien;

    private String sdt;

    private String email;

    private String chucVu;

    // id tài khoản đã tạo trước
    private Integer idTaiKhoan;
}