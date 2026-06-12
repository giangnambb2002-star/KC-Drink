package com.example.datn.tai_khoan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "TAI_KHOAN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaiKhoan {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_tai_khoan")
        private Integer idTaiKhoan;

        @Column(name = "username")
        private String username;

        @Column(name = "password")
        private String password;

        @Column(name = "email")
        private String email;

        @Column(name = "role")
        private String role;

        @Column(name = "trang_thai")
        private Integer trangThai;

        @Column(name = "ngay_tao")
        private LocalDateTime ngayTao;

        @PrePersist
        public void prePersist() {
                this.ngayTao = LocalDateTime.now();
                if (this.trangThai == null) {
                        this.trangThai = 1;
                }
        }

        @Column(name = "ngay_cap_nhat")
        private LocalDateTime ngayCapNhat;
        @PreUpdate
        public void preUpdate() {
                this.ngayCapNhat = LocalDateTime.now();
        }
}
