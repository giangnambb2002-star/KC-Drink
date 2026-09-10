package com.example.datn.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardNhanVienResponse {

    private Integer idNhanVien;
    private String tenNhanVien;
    private long soDon;
    private BigDecimal doanhThu;
}