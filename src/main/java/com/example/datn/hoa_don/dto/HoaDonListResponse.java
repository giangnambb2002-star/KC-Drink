package com.example.datn.hoa_don.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonListResponse {

    private Integer idHoaDon;
    private String maHoaDon;
    private String loaiHoaDon;
    private LocalDateTime ngayTao;

    private BigDecimal tongTien;
    private BigDecimal giamGia;
    private BigDecimal phiVanChuyen;
    private BigDecimal thanhTien;

    private String hinhThucThanhToan;
    private String payosStatus;
    private String trangThai;

    private Integer idKhachHang;
    private String tenKhachHang;
    private String sdtKhachHang;

    private Integer idNhanVien;
    private String tenNhanVien;

    private Boolean coGiaoHang;
    private String maVanDonGhn;
    private String trangThaiVanDon;
    private String trangThaiGhn;
    private LocalDateTime thoiGianGiaoDuKien;
}