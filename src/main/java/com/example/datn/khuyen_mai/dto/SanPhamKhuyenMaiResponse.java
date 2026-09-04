package com.example.datn.khuyen_mai.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SanPhamKhuyenMaiResponse {

    private Integer idSanPham;
    private String tenSanPham;
    private BigDecimal giaGoc;
    private BigDecimal giaSauKhuyenMai;
    private String hinhAnh;
}