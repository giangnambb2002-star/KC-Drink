package com.example.datn.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class DashboardSanPhamBanChayResponse {

    private Integer idSanPham;

    private String tenSanPham;

    private Long soLuongBan;

    private BigDecimal doanhThu;
}