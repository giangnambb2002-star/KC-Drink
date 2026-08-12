package com.example.datn.nhat_ky_he_thong.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "NHAT_KY_HE_THONG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhatKyHeThong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nhat_ky")
    private Integer idNhatKy;

    @Column(name = "id_tai_khoan")
    private Integer idTaiKhoan;

    @Column(name = "username")
    private String username;

    @Column(name = "role")
    private String role;

    @Column(name = "hanh_dong")
    private String hanhDong;

    @Column(name = "doi_tuong")
    private String doiTuong;

    @Column(name = "id_doi_tuong")
    private Integer idDoiTuong;

    @Column(name = "noi_dung")
    private String noiDung;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

    @PrePersist
    public void prePersist() {
        if (this.thoiGian == null) {
            this.thoiGian = LocalDateTime.now();
        }
    }
}