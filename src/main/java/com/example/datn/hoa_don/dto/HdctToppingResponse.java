package com.example.datn.hoa_don.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HdctToppingResponse {
    private Integer idHdctTopping;
    private Integer idTopping;
    private String tenTopping;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
}