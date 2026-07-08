package com.example.datn.topping.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LoToppingResponse {
    private Integer idLoTopping;
    private String maLo;
    private Integer idTopping;
    private String tenTopping;
    private Integer soLuongNhap;
    private Integer soLuongTon;
    private String tenNhanVien;
    private LocalDate hanSuDung;
    private LocalDateTime ngayNhap;
    private Integer trangThai;
}