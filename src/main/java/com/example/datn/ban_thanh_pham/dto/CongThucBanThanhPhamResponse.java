package com.example.datn.ban_thanh_pham.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CongThucBanThanhPhamResponse {

    private Integer idCtBtp;

    private Integer idBanThanhPham;
    private String tenBanThanhPham;
    private String donViThanhPham;

    private Integer idNguyenLieu;
    private String tenNguyenLieu;
    private String donViNguyenLieu;

    private BigDecimal soLuongNguyenLieu;
    private BigDecimal soLuongThanhPham;
}