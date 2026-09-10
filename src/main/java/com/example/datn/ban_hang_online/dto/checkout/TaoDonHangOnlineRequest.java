package com.example.datn.ban_hang_online.dto.checkout;

import lombok.Data;

import java.util.List;

@Data
public class TaoDonHangOnlineRequest {

    /*
     * FE sinh UUID cho MỖI lần đặt đơn mới.
     *
     * Nếu mạng retry / double click và gửi lại cùng UUID
     * -> backend trả lại đơn cũ, không tạo đơn thứ hai.
     */
    private String clientRequestId;

    private Integer idDiaChi;

    private Integer idVoucher;

    private String ghiChuDonHang;

    private String hinhThucThanhToan;

    private List<ChiTietGioHangRequest> items;
}