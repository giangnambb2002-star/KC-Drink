package com.example.datn.hoa_don.entity;

import com.example.datn.khach_hang.entity.KhachHang;
import com.example.datn.nhan_vien.entity.NhanVien;
import com.example.datn.voucher.entity.Voucher;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "HOA_DON")
@Getter
@Setter
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hoa_don")
    private Integer idHoaDon;

    @Column(name = "ma_hoa_don", length = 20)
    private String maHoaDon;

    @Column(name = "client_request_id", length = 100)
    private String clientRequestId;


    @Column(name = "loai_hoa_don", length = 20)
    private String loaiHoaDon;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "tong_tien", precision = 18, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "giam_gia", precision = 18, scale = 2)
    private BigDecimal giamGia;

    @Column(name = "phi_van_chuyen", precision = 18, scale = 2)
    private BigDecimal phiVanChuyen;

    @Column(name = "thanh_tien", precision = 18, scale = 2)
    private BigDecimal thanhTien;

    @Column(name = "hinh_thuc_thanh_toan", length = 50)
    private String hinhThucThanhToan;

    @Column(name = "payos_order_code")
    private Long payosOrderCode;

    @Column(name = "payos_payment_link_id", length = 100)
    private String payosPaymentLinkId;


    @Column(name = "payos_checkout_url", length = 1000)
    private String payosCheckoutUrl;

    @Column(name = "payos_qr_code", columnDefinition = "NVARCHAR(MAX)")
    private String payosQrCode;

    @Column(name = "payos_status", length = 20)
    private String payosStatus;

    @Column(name = "payos_expires_at")
    private LocalDateTime payosExpiresAt;

    @Column(name = "trang_thai", length = 20)
    private String trangThai;

    @Column(name = "dia_chi_giao_hang", length = 255)
    private String diaChiGiaoHang;

    @Column(name = "sdt_nhan_hang", length = 15)
    private String sdtNhanHang;

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_voucher")
    private Voucher voucher;

    @PrePersist
    public void prePersist() {
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
        if (tongTien == null) {
            tongTien = BigDecimal.ZERO;
        }
        if (giamGia == null) {
            giamGia = BigDecimal.ZERO;
        }
        if (phiVanChuyen == null) {
            phiVanChuyen = BigDecimal.ZERO;
        }
        if (thanhTien == null) {
            thanhTien = BigDecimal.ZERO;
        }
    }
}