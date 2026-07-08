package com.example.datn.topping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ToppingRequest {

    @NotBlank(message = "Tên topping không được để trống")
    @Size(max = 100, message = "Tên topping tối đa 100 ký tự")
    private String tenTopping;


    @NotNull(message = "Giá topping không được để trống")
    @PositiveOrZero(message = "Giá topping phải lớn hơn hoặc bằng 0")
    private BigDecimal giaTopping;

    private Integer trangThai;

    // 👉 Đã đồng bộ tên biến
    private Integer tongTonKho;
}