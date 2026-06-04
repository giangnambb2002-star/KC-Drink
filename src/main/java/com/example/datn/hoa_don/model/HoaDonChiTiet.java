package com.example.datn.hoa_don.model;

import com.example.datn.san_pham.model.SanPham;
import com.example.datn.size.model.Size;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "HOA_DON_CHI_TIET")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hdct")
    private Integer idHdct;

    @ManyToOne
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @ManyToOne
    @JoinColumn(name = "id_san_pham")
    private SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "id_size")
    private Size size;

    @Column(name = "ten_san_pham")
    private String tenSanPham;

    @Column(name = "ten_size")
    private String tenSize;

    @Column(name = "ten_topping")
    private String tenTopping;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "don_gia")
    private BigDecimal donGia;

    @Column(name = "thanh_tien")
    private BigDecimal thanhTien;
}