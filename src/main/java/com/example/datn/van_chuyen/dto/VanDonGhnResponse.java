package com.example.datn.van_chuyen.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class VanDonGhnResponse {
    private Integer idVanDon;
    private Integer idHoaDon;
    private Integer idDiaChi;
    private String tenNguoiNhan;
    private String sdtNguoiNhan;
    private String diaChiGiaoHang;
    private Integer provinceId;
    private Integer districtId;
    private String wardCode;
    private String tenTinhThanh;
    private String tenQuanHuyen;
    private String tenPhuongXa;
    private BigDecimal phiVanChuyen;
    private Integer codAmount;
    private Integer insuranceValue;
    private String maVanDonGhn;
    private String trangThai;
    private String trangThaiGhn;
    private LocalDateTime thoiGianGiaoDuKien;
    private String ghiChu;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
}