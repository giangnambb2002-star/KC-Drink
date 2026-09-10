package com.example.datn.ban_hang_online.dto.don_hang;

import com.example.datn.hoa_don.dto.HoaDonChiTietResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDonHangOnlineResponse {

    private Integer idHoaDon;
    private String maHoaDon;
    private LocalDateTime ngayTao;

    private String trangThai;
    private String hinhThucThanhToan;

    private String payosStatus;
    private Long payosOrderCode;
    private LocalDateTime payosExpiresAt;

    private BigDecimal tongTien;

    /*
     * Voucher
     */
    private BigDecimal giamGia;

    /*
     * CTKM sản phẩm
     */
    private BigDecimal giamGiaKhuyenMai;

    private BigDecimal phiVanChuyen;
    private BigDecimal thanhTien;

    private Integer idVoucher;
    private String maVoucher;
    private String tenVoucher;

    private String ghiChu;

    /*
     * Snapshot giao hàng
     */
    private Integer idDiaChi;
    private String tenNguoiNhan;
    private String sdtNguoiNhan;
    private String diaChiGiaoHang;

    private String trangThaiVanDon;
    private String maVanDonGhn;
    private String trangThaiGhn;

    private LocalDateTime thoiGianGiaoDuKien;

    private List<HoaDonChiTietResponse> chiTiet;
}