package com.example.datn.van_chuyen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TinhPhiRequest {

    @NotNull(message = "Mã quận/huyện không được để trống")
    private Integer districtId;

    @NotBlank(message = "Mã phường/xã không được để trống")
    private String wardCode;

    private Integer weight;

    private Integer length;

    private Integer width;

    private Integer height;

    private Integer insuranceValue;
}