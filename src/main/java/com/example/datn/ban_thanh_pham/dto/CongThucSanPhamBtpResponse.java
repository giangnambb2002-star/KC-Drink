package com.example.datn.ban_thanh_pham.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CongThucSanPhamBtpResponse {

    private Integer id;

    private Integer idSanPham;
    private Integer idSize;

    private Integer idBanThanhPham;
    private String tenBanThanhPham;
    private String donViTinh;

    private BigDecimal soLuongCanDung;
}