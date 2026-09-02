package com.example.datn.hoa_don.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ThemToppingRequest {

    @NotNull(message = "Topping không được để trống")
    private Integer idTopping;

    @NotNull(message = "Số lượng topping không được để trống")
    @Min(value = 1, message = "Số lượng topping phải lớn hơn 0")
    private Integer soLuong;

    @NotNull(message = "Đơn giá topping không được để trống")
    private java.math.BigDecimal donGia;
}