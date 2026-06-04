package com.example.datn.hoa_don.model;

import com.example.datn.khach_hang.model.KhachHang;
import com.example.datn.nhan_vien.model.NhanVien;
import com.example.datn.voucher.model.Voucher;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "HOA_DON")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hoa_don")
    private Integer idHoaDon;

    @Column(name = "ma_hoa_don")
    private String maHoaDon;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "tong_tien")
    private BigDecimal tongTien;

    @Column(name = "giam_gia")
    private BigDecimal giamGia;

    @Column(name = "thanh_tien")
    private BigDecimal thanhTien;

    @Column(name = "hinh_thuc_thanh_toan")
    private String hinhThucThanhToan;

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "id_voucher")
    private Voucher voucher;
}