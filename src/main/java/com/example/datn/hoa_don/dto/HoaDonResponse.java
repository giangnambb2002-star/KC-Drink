package com.example.datn.hoa_don.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class HoaDonResponse {
    private Integer idHoaDon;
    private String maHoaDon;
    private String loaiHoaDon;
    private LocalDateTime ngayTao;
    private BigDecimal tongTien;
    private BigDecimal giamGia;
    private BigDecimal phiVanChuyen;
    private BigDecimal thanhTien;
    private String hinhThucThanhToan;
    private Long payosOrderCode;
    private String payosPaymentLinkId;
    private String payosStatus;
    private String trangThai;
    private String ghiChu;
    private Integer idKhachHang;
    private String tenKhachHang;
    private Integer idNhanVien;
    private String tenNhanVien;
    private Integer idVoucher;
    private String maVoucher;
    private String tenVoucher;
    private List<HoaDonChiTietResponse> chiTiet;
}