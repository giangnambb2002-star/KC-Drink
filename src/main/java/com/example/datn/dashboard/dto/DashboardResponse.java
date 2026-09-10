package com.example.datn.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    // Tổng quan hệ thống
    private long tongKhachHang;
    private long tongNhanVien;
    private long voucherDangHoatDong;
    private long toppingDangBan;
    private long nguyenLieuDangHoatDong;

    // Cảnh báo kho
    private long loToppingSapHetHan;
    private long loNguyenLieuSapHetHan;
    private long nguyenLieuDuoiNguong;

    // Kinh doanh hôm nay
    private BigDecimal doanhThuHomNay;
    private long donOnlineHomNay;
    private long donOfflineHomNay;
    private long donDaThanhToanHomNay;
    private long donDaHuyHomNay;

    // Vận hành đơn online
    private long donChoTiepNhan;
    private long donDaTiepNhan;
    private long donDangGiao;
    private long donDaGiao;

    private List<DashboardNgayResponse> bieuDo7Ngay;

    private List<DashboardSanPhamBanChayResponse> topSanPham7Ngay;

    private List<DashboardNhanVienResponse> thongKeNhanVien7Ngay;
}