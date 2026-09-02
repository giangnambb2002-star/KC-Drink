package com.example.datn.san_pham.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SanPhamSizeRequest {

    @NotNull(message = "Sản phẩm không được để trống")
    private Integer idSanPham;

    @NotNull(message = "Size không được để trống")
    private Integer idSize;

    @DecimalMin(value = "0", message = "Phụ thu không được nhỏ hơn 0")
    private BigDecimal   phuThu;
}