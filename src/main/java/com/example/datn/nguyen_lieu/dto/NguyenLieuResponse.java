package com.example.datn.nguyen_lieu.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NguyenLieuResponse {
    private Integer idNguyenLieu;
    private String tenNguyenLieu;
    private String donViTinh;
    private Integer trangThai;
    private BigDecimal tongTonKho;
    private BigDecimal nguongTonKho;
}