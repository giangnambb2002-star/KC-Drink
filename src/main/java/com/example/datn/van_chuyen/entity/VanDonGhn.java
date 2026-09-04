package com.example.datn.van_chuyen.entity;

import com.example.datn.dia_chi.entity.DiaChiKhachHang;
import com.example.datn.hoa_don.entity.HoaDon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "VAN_DON_GHN",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_VAN_DON_GHN_HOA_DON",
                columnNames = "id_hoa_don"
        )
)
@Getter
@Setter
public class VanDonGhn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_van_don")
    private Integer idVanDon;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_hoa_don", nullable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dia_chi")
    private DiaChiKhachHang diaChiKhachHang;

    @Column(name = "ten_nguoi_nhan", nullable = false, length = 100)
    private String tenNguoiNhan;

    @Column(name = "sdt_nguoi_nhan", nullable = false, length = 15)
    private String sdtNguoiNhan;

    @Column(name = "dia_chi_giao_hang", nullable = false, length = 255)
    private String diaChiGiaoHang;

    @Column(name = "province_id")
    private Integer provinceId;

    @Column(name = "district_id", nullable = false)
    private Integer districtId;

    @Column(name = "ward_code", nullable = false, length = 20)
    private String wardCode;

    @Column(name = "ten_tinh_thanh", length = 100)
    private String tenTinhThanh;

    @Column(name = "ten_quan_huyen", length = 100)
    private String tenQuanHuyen;

    @Column(name = "ten_phuong_xa", length = 100)
    private String tenPhuongXa;

    @Column(name = "phi_van_chuyen", precision = 18, scale = 2)
    private BigDecimal phiVanChuyen;

    @Column(name = "cod_amount")
    private Integer codAmount;

    @Column(name = "insurance_value")
    private Integer insuranceValue;

    @Column(name = "ma_van_don_ghn", length = 50)
    private String maVanDonGhn;

    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai;

    @Column(name = "trang_thai_ghn", length = 50)
    private String trangThaiGhn;

    @Column(name = "thoi_gian_giao_du_kien")
    private LocalDateTime thoiGianGiaoDuKien;

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (trangThai == null) trangThai = "CHO_THANH_TOAN";
        if (phiVanChuyen == null) phiVanChuyen = BigDecimal.ZERO;
        if (codAmount == null) codAmount = 0;
        if (insuranceValue == null) insuranceValue = 0;
        if (ngayTao == null) ngayTao = now;
        ngayCapNhat = now;
    }

    @PreUpdate
    public void preUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}