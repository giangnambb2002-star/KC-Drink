package com.example.datn.nhan_vien.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NhanVienUpdateRequest {

    @NotBlank(message = "Tên nhân viên không được để trống")
    @Size(
            min = 3,
            max = 50,
            message = "Tên nhân viên phải từ 3 đến 50 ký tự"
    )
    private String tenNhanVien;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
            regexp = "^0[35789][0-9]{8}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String sdt;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(
            max = 100,
            message = "Email tối đa 100 ký tự"
    )
    private String email;

    @NotBlank(message = "Chức vụ không được để trống")
    @Pattern(
            regexp = "^(ADMIN|STAFF)$",
            message = "Chức vụ chỉ được là ADMIN hoặc STAFF"
    )
    private String chucVu;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(
            min = 5,
            max = 30,
            message = "Tên đăng nhập phải từ 5 đến 30 ký tự"
    )
    @Pattern(
            regexp = "^[A-Za-z0-9_]+$",
            message = "Tên đăng nhập chỉ được chứa chữ, số và dấu gạch dưới"
    )
    private String username;
}