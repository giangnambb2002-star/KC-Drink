package com.example.datn.nhan_vien.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class NhanVienRequest {

    @NotBlank(message = "Tên nhân viên không được để trống")
    private String tenNhanVien;

    @Pattern(
            regexp = "^0\\d{9}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String sdt;

    @Email(message = "Email không hợp lệ")
    private String email;

    private String chucVu;

    // id tài khoản đã tạo trước
    private Integer idTaiKhoan;
}