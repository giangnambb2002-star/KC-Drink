package com.example.datn.khach_hang.entity;

import com.example.datn.tai_khoan.entity.TaiKhoan;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "KHACH_HANG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_khach_hang")
    private Integer idKhachHang;

    @Column(name = "ten_khach_hang")
    private String tenKhachHang;

    @Column(name = "sdt")
    private String sdt;

    @Column(name = "email")
    private String email;

    @Column(name = "dia_chi")
    private String diaChi;

    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "diem_tich_luy")
    private Integer diemTichLuy;

    @OneToOne
    @JoinColumn(name = "id_tai_khoan")
    private TaiKhoan taiKhoan;
}