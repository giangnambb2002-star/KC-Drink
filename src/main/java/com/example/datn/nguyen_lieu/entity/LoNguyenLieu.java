package com.example.datn.nguyen_lieu.entity;

import com.example.datn.nhan_vien.entity.NhanVien;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "LO_NGUYEN_LIEU")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoNguyenLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lo")
    private Integer idLo;

    @ManyToOne
    @JoinColumn(name = "id_nguyen_lieu")
    private NguyenLieu nguyenLieu;

    @Column(name = "ma_lo")
    private String maLo;

    @Column(name = "so_luong_ton")
    private Double soLuongTon;

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