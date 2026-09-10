package com.example.datn.ban_hang_online.dto.don_hang;

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
public class DonHangCuaToiResponse {

    private Integer idHoaDon;
    private String maHoaDon;
    private LocalDateTime ngayTao;

    private String trangThai;
    private String payosStatus;

    private BigDecimal tongTien;
    private BigDecimal giamGia;
    private BigDecimal phiVanChuyen;
    private BigDecimal thanhTien;

    private String trangThaiVanDon;
    private String maVanDonGhn;
    private String trangThaiGhn;
}