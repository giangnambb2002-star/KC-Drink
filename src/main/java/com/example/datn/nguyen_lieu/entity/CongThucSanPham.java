package com.example.datn.nguyen_lieu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CONG_THUC_SAN_PHAM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CongThucSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ctsp")
    private Integer idCtsp;

    @Column(name = "id_san_pham")
    private Integer idSanPham;

    @Column(name = "id_size")
    private Integer idSize;

    @ManyToOne
    @JoinColumn(name = "id_nguyen_lieu")
    private NguyenLieu nguyenLieu;

    @Column(name = "so_luong_can_dung")
    private Double soLuongCanDung;
}