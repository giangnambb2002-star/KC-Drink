package com.example.datn.ban_hang_online.dto.checkout;

import lombok.Data;

import java.util.List;

@Data
public class ChiTietGioHangRequest {

    private Integer idSanPham;

    private Integer idSize;

    private Integer mucDuong;

    private Integer mucDa;

    private Integer soLuong;

    private String ghiChu;

    private List<ToppingGioHangRequest> toppings;
}