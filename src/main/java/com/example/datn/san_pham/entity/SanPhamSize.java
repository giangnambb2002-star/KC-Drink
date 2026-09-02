package com.example.datn.san_pham.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "SAN_PHAM_SIZE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "id_san_pham")
    private Integer idSanPham;

    @ManyToOne
    @JoinColumn(name = "id_size")
    private Size size;

    @Column(name = "phu_thu", nullable = false)
    private BigDecimal phuThu = BigDecimal.ZERO;
}