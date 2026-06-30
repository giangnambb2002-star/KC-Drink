package com.example.datn.dia_chi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DiaChiRequest {

    @NotNull(message = "Khách hàng không được để trống")
    private Integer idKhachHang;

    @NotBlank(message = "Tên người nhận không được để trống")
    private String tenNguoiNhan;

    @Pattern(
            regexp = "^0[3|5|7|8|9][0-9]{8}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String sdtNguoiNhan;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String diaChi;

    private Boolean macDinh;

}