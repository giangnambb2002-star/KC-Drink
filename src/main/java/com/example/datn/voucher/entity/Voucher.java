package com.example.datn.voucher.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "VOUCHER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_voucher")
    private Integer idVoucher;

    @Column(name = "ma_voucher")
    private String maVoucher;

    @Column(name = "ten_voucher")
    private String tenVoucher;

    @Column(name = "gia_tri_giam")
    private BigDecimal giaTriGiam;

    @Column(name = "dieu_kien")
    private BigDecimal dieuKien;

    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDateTime ngayKetThuc;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "trang_thai")
    private Integer trangThai; // 1: Chưa dùng, 0: Đã dùng

    // ================ CÁC TRƯỜNG THÊM MỚI ĐỂ XỬ LÝ NGHIỆP VỤ ================
    @Column(name = "id_khach_hang")
    private Integer idKhachHang;

    @Column(name = "loai_voucher")
    private String loaiVoucher; // "PERCENT" hoặc "FIXED"

    @Column(name = "giam_toi_da")
    private BigDecimal giamToiDa;
}