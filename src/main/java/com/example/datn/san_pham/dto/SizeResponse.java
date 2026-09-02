package com.example.datn.san_pham.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SizeResponse {

    private Integer idSize;
    private String tenSize;
    private BigDecimal phuThu;
    private Integer thuTu;
}