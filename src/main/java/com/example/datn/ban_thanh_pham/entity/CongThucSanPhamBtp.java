package com.example.datn.ban_thanh_pham.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "CONG_THUC_SAN_PHAM_BTP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CongThucSanPhamBtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "id_san_pham")
    private Integer idSanPham;

    @Column(name = "id_size")
    private Integer idSize;

    @ManyToOne
    @JoinColumn(name = "id_ban_thanh_pham")
    private BanThanhPham banThanhPham;

    @Column(name = "so_luong_can_dung")
    private BigDecimal soLuongCanDung;
}