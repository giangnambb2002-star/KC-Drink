package com.example.datn.hoa_don.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "HDCT_TOPPING")
@Getter
@Setter
public class HdctTopping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idHdctTopping;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hdct")
    private HoaDonChiTiet hoaDonChiTiet;

    @Column(name = "id_topping")
    private Integer idTopping;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "don_gia", precision = 18, scale = 2, nullable = false)
    private BigDecimal donGia;

    @Column(name = "thanh_tien", precision = 18, scale = 2, nullable = false)
    private BigDecimal thanhTien;

    @PrePersist
    @PreUpdate
    public void calculateThanhTien() {
        if (donGia != null && soLuong != null) {
            thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
        }
    }
}