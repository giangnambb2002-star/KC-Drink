package com.example.datn.ban_hang.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    private Integer sanPhamId;

    private Integer sizeId;

    private Integer soLuong;

    private String tenSanPham;

    private String tenSize;

    private String tenTopping;

    private BigDecimal donGia;

    private BigDecimal thanhTien;
}