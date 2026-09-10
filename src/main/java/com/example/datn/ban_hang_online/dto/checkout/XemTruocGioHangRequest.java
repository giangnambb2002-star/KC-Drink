package com.example.datn.ban_hang_online.dto.checkout;

import lombok.Data;

import java.util.List;

@Data
public class XemTruocGioHangRequest {

    private Integer idDiaChi;

    private Integer idVoucher;

    private List<ChiTietGioHangRequest> items;
}