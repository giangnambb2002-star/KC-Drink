package com.example.datn.khuyen_mai.dto;

import com.example.datn.khuyen_mai.enums.LoaiGiamKhuyenMai;
import com.example.datn.khuyen_mai.enums.TrangThaiKhuyenMai;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class KhuyenMaiResponse {

    private Integer idKm;
    private String tenKm;
    private LoaiGiamKhuyenMai loaiGiam;
    private BigDecimal giaTriGiam;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private Integer trangThai;
    private TrangThaiKhuyenMai trangThaiHienThi;
    private String moTa;
    private Integer soLuongSanPham;
    private List<SanPhamKhuyenMaiResponse> sanPhamApDung;
}