package com.example.datn.ban_thanh_pham.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CongThucSanPhamBtpRequest {

    @NotNull(message = "Sản phẩm không được để trống")
    private Integer idSanPham;

    @NotNull(message = "Size không được để trống")
    private Integer idSize;

    @NotNull(message = "Bán thành phẩm không được để trống")
    private Integer idBanThanhPham;

    @NotNull(message = "Số lượng cần dùng không được để trống")
    @DecimalMin(value = "0.01", message = "Số lượng cần dùng phải lớn hơn 0")
    private BigDecimal soLuongCanDung;
}