package com.example.datn.khuyen_mai.entity;

import com.example.datn.san_pham.entity.SanPham;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "SAN_PHAM_KHUYEN_MAI",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_SAN_PHAM_KHUYEN_MAI",
                        columnNames = {"id_san_pham", "id_km"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamKhuyenMai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_san_pham", nullable = false)
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_km", nullable = false)
    private KhuyenMai khuyenMai;
}