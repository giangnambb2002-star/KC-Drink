package com.example.datn.hoa_don.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HoaDonChiTietResponse {
    private Integer idHoaDonChiTiet;
    private Integer idSanPham;
    private String tenSanPham;
    private Integer idSize;
    private String tenSize;
    private Integer soLuong;
    private BigDecimal giaGoc;
    private BigDecimal tienGiamKhuyenMai;
    private Integer idKm;
    private String tenKhuyenMai;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private Integer mucDuong;
    private Integer mucDa;
    private String ghiChu;
    private List<HdctToppingResponse> toppingList;
}