package com.example.datn.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MeResponse {

    private Integer idTaiKhoan;

    private String username;

    private String email;

    private String role;

    private String tenNguoiDung;

    private String chucVu;

    private Integer diemTichLuy;
}