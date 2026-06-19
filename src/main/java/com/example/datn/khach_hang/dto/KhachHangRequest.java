package com.example.datn.khach_hang.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class KhachHangRequest {

    @NotBlank(message = "Tên khách hàng không được để trống")
    @Size(max = 100, message = "Tên khách hàng tối đa 100 ký tự")
    private String tenKhachHang;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
            regexp = "^(0[3|5|7|8|9])[0-9]{8}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String sdt;

    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email tối đa 100 ký tự")
    private String email;

    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    private String diaChi;

    private Boolean gioiTinh;

    @Past(message = "Ngày sinh phải nhỏ hơn ngày hiện tại")
    private LocalDate ngaySinh;

    @Min(value = 0, message = "Điểm tích lũy không được âm")
    private Integer diemTichLuy;

    // Có thể null vì khách lẻ
    private Integer idTaiKhoan;
}