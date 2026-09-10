package com.example.datn.ban_hang_online.dto.checkout;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToppingGioHangResponse {

    private Integer idTopping;

    private String tenTopping;

    private Integer soLuong;

    private BigDecimal donGia;

    private BigDecimal thanhTien;
}