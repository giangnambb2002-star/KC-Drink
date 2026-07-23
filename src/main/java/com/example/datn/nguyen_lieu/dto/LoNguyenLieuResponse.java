package com.example.datn.nguyen_lieu.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LoNguyenLieuResponse {
    private Integer idLo;
    private String maLo;
    private Integer idNguyenLieu;
    private String tenNguyenLieu; // Lấy từ bảng cha
    private String donViTinh;     // Lấy từ bảng cha
    private Double soLuongTon;
    private String tenNhanVien; // Lấy tên cho FE đỡ phải gọi API 2 lần
    private LocalDate hanSuDung;
    private LocalDateTime ngayNhap;
    private Integer trangThai;
    private String trangThaiHsd;
}