    package com.example.datn.ban_thanh_pham.entity;

    import com.example.datn.nguyen_lieu.entity.NguyenLieu;
    import jakarta.persistence.*;
    import lombok.*;

    import java.math.BigDecimal;

    @Entity
    @Table(name = "CONG_THUC_BAN_THANH_PHAM")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class CongThucBanThanhPham {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_ct_btp")
        private Integer idCtBtp;

        @ManyToOne
        @JoinColumn(name = "id_ban_thanh_pham")
        private BanThanhPham banThanhPham;

        @ManyToOne
        @JoinColumn(name = "id_nguyen_lieu")
        private NguyenLieu nguyenLieu;

        @Column(name = "so_luong_nguyen_lieu")
        private BigDecimal soLuongNguyenLieu;

        @Column(name = "so_luong_thanh_pham")
        private BigDecimal soLuongThanhPham;
    }