package com.example.datn.ban_hang_online.entity;

import com.example.datn.hoa_don.entity.HoaDon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "DAT_CHO_KHO",
        uniqueConstraints = @UniqueConstraint(
                name = "UX_DAT_CHO_KHO_HOA_DON_RESOURCE",
                columnNames = {"id_hoa_don", "loai_kho", "id_doi_tuong"}
        )
)
@Getter
@Setter
public class DatChoKho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dat_cho")
    private Integer idDatCho;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_hoa_don", nullable = false)
    private HoaDon hoaDon;

    @Column(name = "loai_kho", nullable = false, length = 30)
    private String loaiKho;

    @Column(name = "id_doi_tuong", nullable = false)
    private Integer idDoiTuong;

    @Column(name = "so_luong", nullable = false, precision = 18, scale = 3)
    private BigDecimal soLuong;

    @Column(name = "trang_thai", nullable = false, length = 20)
    private String trangThai;

    @Column(name = "het_han_luc", nullable = false)
    private LocalDateTime hetHanLuc;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (trangThai == null) {
            trangThai = "ACTIVE";
        }

        if (ngayTao == null) {
            ngayTao = now;
        }

        ngayCapNhat = now;
    }

    @PreUpdate
    public void preUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}