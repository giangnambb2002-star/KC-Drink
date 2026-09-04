package com.example.datn.hoa_don.entity;

import com.example.datn.khuyen_mai.entity.KhuyenMai;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "HOA_DON_CHI_TIET")
@Getter
@Setter
public class HoaDonChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hdct")
    private Integer idHoaDonChiTiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @Column(name = "id_san_pham")
    private Integer idSanPham;

    @Column(name = "id_size")
    private Integer idSize;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "gia_goc", precision = 18, scale = 2)
    private BigDecimal giaGoc;

    @Column(name = "don_gia", precision = 18, scale = 2)
    private BigDecimal donGia;

    @Column(
            name = "tien_giam_khuyen_mai",
            precision = 18,
            scale = 2,
            nullable = false
    )
    private BigDecimal tienGiamKhuyenMai = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_km")
    private KhuyenMai khuyenMai;

    @Column(name = "thanh_tien", precision = 18, scale = 2)
    private BigDecimal thanhTien;

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    @Column(name = "muc_duong")
    private Integer mucDuong;

    @Column(name = "muc_da")
    private Integer mucDa;

    @PrePersist
    @PreUpdate
    public void calculateThanhTien() {
        if (giaGoc == null) {
            giaGoc = donGia;
        }

        if (donGia != null && soLuong != null) {
            thanhTien = donGia.multiply(
                    BigDecimal.valueOf(soLuong)
            );
        }

        if (giaGoc != null
                && donGia != null
                && soLuong != null) {
            BigDecimal giamMoiDonVi = giaGoc
                    .subtract(donGia)
                    .max(BigDecimal.ZERO);

            tienGiamKhuyenMai = giamMoiDonVi.multiply(
                    BigDecimal.valueOf(soLuong)
            );
        } else if (tienGiamKhuyenMai == null) {
            tienGiamKhuyenMai = BigDecimal.ZERO;
        }
    }
}