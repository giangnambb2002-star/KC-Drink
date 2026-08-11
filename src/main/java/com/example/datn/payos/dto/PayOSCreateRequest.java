package com.example.datn.payos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PayOSCreateRequest {

    @Min(value = 1000, message = "Số tiền tối thiểu là 1.000 đồng")
    private Long amount;

    @NotBlank(message = "Nội dung thanh toán không được để trống")
    private String description;
}