package com.example.datn.ban_hang.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThanhToanRequest {

    private Integer khachHangId;

    private String sdt;

    private String tenKhachHang;

    private String ghiChu;

    private Integer voucherId;

    private List<CartItem> items;
}