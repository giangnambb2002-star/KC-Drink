package com.example.datn.hoa_don.dto;

import lombok.Data;

@Data
public class ThemMonRequest {
    private Integer idSanPham;
    private Integer idSize;
    private Integer soLuong;
    private Integer mucDuong;
    private Integer mucDa;
    private String ghiChu;
}