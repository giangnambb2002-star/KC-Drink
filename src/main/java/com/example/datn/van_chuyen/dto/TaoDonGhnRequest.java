package com.example.datn.van_chuyen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaoDonGhnRequest {

    @NotBlank(message = "Tên người nhận không được để trống")
    private String toName;

    @NotBlank(message = "Số điện thoại người nhận không được để trống")
    private String toPhone;

    @NotBlank(message = "Địa chỉ người nhận không được để trống")
    private String toAddress;

    @NotNull(message = "Mã quận/huyện không được để trống")
    private Integer toDistrictId;

    @NotBlank(message = "Mã phường/xã không được để trống")
    private String toWardCode;

    private Integer codAmount;

    private Integer insuranceValue;

    private String note;

    @NotBlank(message = "Tên phường/xã không được để trống")
    private String toWardName;

    @NotBlank(message = "Tên quận/huyện không được để trống")
    private String toDistrictName;

    @NotBlank(message = "Tên tỉnh/thành phố không được để trống")
    private String toProvinceName;
}