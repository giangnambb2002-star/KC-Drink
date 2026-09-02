package com.example.datn.ban_thanh_pham.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "BAN_THANH_PHAM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BanThanhPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ban_thanh_pham")
    private Integer idBanThanhPham;

    @Column(name = "ten_ban_thanh_pham")
    private String tenBanThanhPham;

    @Column(name = "don_vi_tinh")
    private String donViTinh;

    @Column(name = "han_su_dung_gio")
    private Integer hanSuDungGio;

    @Column(name = "trang_thai")
    private Integer trangThai;
}