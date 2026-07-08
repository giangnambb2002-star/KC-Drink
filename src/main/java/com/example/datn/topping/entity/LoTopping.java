package com.example.datn.topping.entity;

import com.example.datn.nhan_vien.entity.NhanVien;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "LO_TOPPING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoTopping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lo_topping")
    private Integer idLoTopping;

    @Column(name = "ma_lo")
    private String maLo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_topping")
    private Topping topping;

    @Column(name = "so_luong_nhap")
    private Integer soLuongNhap;

    @Column(name = "so_luong_ton")
    private Integer soLuongTon; // Bị trừ dần khi bán

    @Column(name = "han_su_dung")
    private LocalDate hanSuDung;

    @Column(name = "ngay_nhap")
    private LocalDateTime ngayNhap;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;
}