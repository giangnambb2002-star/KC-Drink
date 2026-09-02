package com.example.datn.ban_thanh_pham.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MePhaCheResponse {

    private Integer idMePha;

    private Integer idBanThanhPham;
    private String tenBanThanhPham;
    private String donViTinh;

    private Integer idNhanVien;
    private String tenNhanVien;

    private BigDecimal soLuongTaoRa;
    private BigDecimal soLuongConLai;

    private LocalDateTime ngayPha;
    private LocalDateTime hanSuDung;

    private Integer trangThai;
    private String ghiChu;
}