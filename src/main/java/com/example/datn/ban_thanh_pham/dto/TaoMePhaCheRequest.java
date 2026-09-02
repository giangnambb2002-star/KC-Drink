package com.example.datn.ban_thanh_pham.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaoMePhaCheRequest {

    @NotNull(message = "Bán thành phẩm không được để trống")
    private Integer idBanThanhPham;

    private Integer idNhanVien;

    @NotNull(message = "Số lượng muốn pha không được để trống")
    @DecimalMin(value = "0.01", message = "Số lượng muốn pha phải lớn hơn 0")
    private BigDecimal soLuongTaoRa;

    private String ghiChu;
}