package com.example.datn.ban_hang_online.dto.checkout;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VoucherOnlineResponse {

    private Integer idVoucher;

    private String maVoucher;

    private String tenVoucher;

    private String loaiVoucher;

    private BigDecimal giaTriGiam;

    private BigDecimal giamToiDa;

    private BigDecimal dieuKien;

    /*
     * Số tiền dự kiến voucher giảm với tổng tiền
     * được truyền vào GET /api/online/vouchers.
     *
     * Đây chỉ phục vụ hiển thị.
     * Checkout preview vẫn validate/tính lại.
     */
    private BigDecimal soTienGiam;

    private Integer soLuong;

    /*
     * null  -> voucher hệ thống
     * !=null -> voucher riêng khách hàng
     */
    private Integer idKhachHang;

    private LocalDateTime ngayBatDau;

    private LocalDateTime ngayKetThuc;
}