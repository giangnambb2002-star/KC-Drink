package com.example.datn.san_pham.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SanPhamResponse {
    private Integer idSanPham;
    private String tenSanPham;
    private Integer idKm;
    private String tenKhuyenMai;
    private BigDecimal tienGiamKhuyenMai;
    private BigDecimal giaSauKhuyenMai;
    private Boolean coKhuyenMai;
    private BigDecimal gia;
    private String moTa;
    private String hinhAnh;
    private Integer trangThai;
    private Integer idDanhMuc;
}