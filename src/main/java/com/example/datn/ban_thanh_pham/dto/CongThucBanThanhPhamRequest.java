package com.example.datn.ban_thanh_pham.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CongThucBanThanhPhamRequest {

    @NotNull(message = "Bán thành phẩm không được để trống")
    private Integer idBanThanhPham;

    @NotNull(message = "Nguyên liệu không được để trống")
    private Integer idNguyenLieu;

    @NotNull(message = "Số lượng nguyên liệu không được để trống")
    @DecimalMin(value = "0.01", message = "Số lượng nguyên liệu phải lớn hơn 0")
    private BigDecimal soLuongNguyenLieu;

    @NotNull(message = "Số lượng thành phẩm không được để trống")
    @DecimalMin(value = "0.01", message = "Số lượng thành phẩm phải lớn hơn 0")
    private BigDecimal soLuongThanhPham;
}