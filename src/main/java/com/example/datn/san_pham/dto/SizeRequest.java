package com.example.datn.san_pham.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SizeRequest {

    @NotBlank(message = "Tên size không được để trống")
    private String tenSize;

    @NotNull(message = "Phụ thu không được để trống")
    @DecimalMin(value = "0", message = "Phụ thu không được nhỏ hơn 0")
    private BigDecimal phuThu;

    @NotNull(message = "Thứ tự không được để trống")
    private Integer thuTu;
}