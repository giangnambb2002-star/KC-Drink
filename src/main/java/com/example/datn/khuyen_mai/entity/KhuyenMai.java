package com.example.datn.khuyen_mai.entity;

import com.example.datn.khuyen_mai.enums.LoaiGiamKhuyenMai;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "KHUYEN_MAI")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_km")
    private Integer idKm;

    @Column(name = "ten_km", length = 100, nullable = false)
    private String tenKm;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_giam", length = 20, nullable = false)
    private LoaiGiamKhuyenMai loaiGiam;

    @Column(name = "gia_tri_giam", precision = 18, scale = 2, nullable = false)
    private BigDecimal giaTriGiam;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDateTime ngayKetThuc;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai = 1;

    @Column(name = "mo_ta", length = 255)
    private String moTa;

    @PrePersist
    public void prePersist() {
        if (trangThai == null) {
            trangThai = 1;
        }
    }
}