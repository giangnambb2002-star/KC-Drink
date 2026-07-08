package com.example.datn.topping.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ToppingResponse {
    private Integer idTopping;
    private String tenTopping;
    private BigDecimal giaTopping;

    // 👉 Đã đồng bộ tên biến
    private Integer tongTonKho;

    private Integer trangThai;
}