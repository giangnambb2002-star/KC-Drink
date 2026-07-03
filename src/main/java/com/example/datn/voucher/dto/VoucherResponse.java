package com.example.datn.voucher.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VoucherResponse {
    private Integer idVoucher;
    private String maVoucher;
    private String tenVoucher;
    private BigDecimal giaTriGiam;
    private BigDecimal dieuKien;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private Integer soLuong;
    private Integer trangThai;
    private Integer idKhachHang;
    private String loaiVoucher;
    private BigDecimal giamToiDa;
}