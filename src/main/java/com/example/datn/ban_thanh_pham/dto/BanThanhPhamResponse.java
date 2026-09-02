package com.example.datn.ban_thanh_pham.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BanThanhPhamResponse {

    private Integer idBanThanhPham;
    private String tenBanThanhPham;
    private String donViTinh;
    private Integer hanSuDungGio;
    private Integer trangThai;
    private BigDecimal tongTon;
}