package com.example.datn.voucher.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VoucherRequest {
    // ==========================================
    // CÁC TRƯỜNG DÀNH CHO ADMIN THÊM / SỬA MÃ
    // ==========================================
    private String maVoucher;
    private String tenVoucher;

    // Khớp với form Vue: 0 là Giảm theo %, 1 là Giảm tiền mặt
    private Integer kieuGiamGia;
    private BigDecimal giamGia;

    private BigDecimal dieuKien; // Giá trị đơn hàng tối thiểu
    private Integer soLuong;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;

    // ==========================================
    // CÁC TRƯỜNG DÙNG CHUNG VÀ CHO KHÁCH HÀNG CHECKOUT
    // ==========================================
    private Integer idKhachHang; // Dùng để Admin chỉ định mã cá nhân, HOẶC để khách truyền lên lúc thanh toán

    private BigDecimal tongTienDonHang; // Tổng tiền khách có trong giỏ (Dùng cho hàm validate)
}