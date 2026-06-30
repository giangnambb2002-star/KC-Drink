package com.example.datn.nhan_vien.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NhanVienRequest {

    @NotBlank(message = "Tên nhân viên không được để trống")
    @Size(min = 3, max = 50, message = "Tên nhân viên từ 3 - 50 ký tự")
    private String tenNhanVien;

    @Pattern(
            regexp = "^0[3|5|7|8|9][0-9]{8}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String sdt;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Chức vụ không được để trống")
    private String chucVu;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 5, max = 30)
    private String username;

//    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu tối thiểu 6 ký tự")
    private String password;
}