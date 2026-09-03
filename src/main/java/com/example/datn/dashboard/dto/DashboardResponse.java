package com.example.datn.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private long tongKhachHang;
    private long tongNhanVien;
    private long voucherDangHoatDong;
    private long toppingDangBan;
    private long nguyenLieuDangHoatDong;
    private long loToppingSapHetHan;
    private long loNguyenLieuSapHetHan;
    private long nguyenLieuDuoiNguong;
}