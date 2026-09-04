package com.example.datn.khuyen_mai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class KetQuaKhuyenMaiResponse {

    private Integer idKm;
    private String tenKm;
    private BigDecimal giaGoc;
    private BigDecimal tienGiam;
    private BigDecimal giaSauKhuyenMai;
}