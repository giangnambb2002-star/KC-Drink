package com.example.datn.ban_thanh_pham.entity;

import com.example.datn.nhan_vien.entity.NhanVien;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ME_PHA_CHE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MePhaChe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_me_pha")
    private Integer idMePha;

    @ManyToOne
    @JoinColumn(name = "id_ban_thanh_pham")
    private BanThanhPham banThanhPham;

    @ManyToOne
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

    @Column(name = "so_luong_tao_ra")
    private BigDecimal soLuongTaoRa;

    @Column(name = "so_luong_con_lai")
    private BigDecimal soLuongConLai;

    @Column(name = "ngay_pha")
    private LocalDateTime ngayPha;

    @Column(name = "han_su_dung")
    private LocalDateTime hanSuDung;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "ghi_chu")
    private String ghiChu;
}