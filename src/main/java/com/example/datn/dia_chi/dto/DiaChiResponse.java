package com.example.datn.dia_chi.dto;

import lombok.Data;

@Data
public class DiaChiResponse {

    private Integer idDiaChi;

    private Integer idKhachHang;

    private String tenNguoiNhan;

    private String sdtNguoiNhan;

    private String diaChi;

    private Boolean macDinh;

    private Integer trangThai;

    private Integer provinceId;

    private Integer districtId;

    private String wardCode;

    private String tenTinhThanh;

    private String tenQuanHuyen;

    private String tenPhuongXa;

}