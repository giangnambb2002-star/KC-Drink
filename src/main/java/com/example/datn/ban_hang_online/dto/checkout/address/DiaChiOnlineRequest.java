package com.example.datn.ban_hang_online.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DiaChiOnlineRequest {

    @NotBlank(message = "Tên người nhận không được để trống")
    private String tenNguoiNhan;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
            regexp = "^0[35789][0-9]{8}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String sdtNguoiNhan;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String diaChi;

    private Boolean macDinh;

    @NotNull(message = "Tỉnh/thành phố không được để trống")
    private Integer provinceId;

    @NotNull(message = "Quận/huyện không được để trống")
    private Integer districtId;

    @NotBlank(message = "Phường/xã không được để trống")
    private String wardCode;

    @NotBlank(message = "Tên tỉnh/thành phố không được để trống")
    private String tenTinhThanh;

    @NotBlank(message = "Tên quận/huyện không được để trống")
    private String tenQuanHuyen;

    @NotBlank(message = "Tên phường/xã không được để trống")
    private String tenPhuongXa;
}