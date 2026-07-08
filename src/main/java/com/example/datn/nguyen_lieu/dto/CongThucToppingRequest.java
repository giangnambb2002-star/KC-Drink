package com.example.datn.nguyen_lieu.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CongThucToppingRequest {
    @NotNull(message = "Thiếu ID Topping")
    private Integer idTopping;

    @NotNull(message = "Thiếu ID Nguyên liệu")
    private Integer idNguyenLieu;

    @NotNull(message = "Số lượng cần dùng không được để trống")
    @Min(value = 0, message = "Số lượng phải lớn hơn 0")
    private Double soLuongCanDung;
}