package com.example.datn.san_pham.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "SAN_PHAM_CHI_TIET")
@Data
public class SanPhamChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_spct")
    private Integer idSpct;

    @Column(name = "id_san_pham")
    private Integer idSanPham;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "ngay_tao")
    private LocalDate ngayTao;
}
