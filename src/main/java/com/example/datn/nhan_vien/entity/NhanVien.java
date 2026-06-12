package com.example.datn.nhan_vien.entity;

import com.example.datn.tai_khoan.entity.TaiKhoan;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "NHAN_VIEN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NhanVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nhan_vien")
    private Integer idNhanVien;

    @Column(name = "ten_nhan_vien")
    private String tenNhanVien;

    @Column(name = "sdt")
    private String sdt;

    @Column(name = "email")
    private String email;

    @Column(name = "chuc_vu")
    private String chucVu;

    @OneToOne
    @JoinColumn(name = "id_tai_khoan")
    private TaiKhoan taiKhoan;
}
