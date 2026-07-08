package com.example.datn.nguyen_lieu.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;
import java.math.BigDecimal; // hoặc Double tuỳ kiểu dữ liệu team bro dùng

@Entity
@Table(name = "NGUYEN_LIEU")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NguyenLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nguyen_lieu")
    private Integer idNguyenLieu;

    @Column(name = "ten_nguyen_lieu")
    private String tenNguyenLieu;

    @Column(name = "don_vi_tinh")
    private String donViTinh;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Formula("(SELECT COALESCE(SUM(l.so_luong_ton), 0) FROM lo_nguyen_lieu l WHERE l.id_nguyen_lieu = id_nguyen_lieu AND l.trang_thai = 1)")
    private BigDecimal tongTonKho;

    @Column(name = "nguong_ton_kho")
    private BigDecimal nguongTonKho;
}