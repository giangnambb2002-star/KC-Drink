package com.example.datn.hoa_don.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VoucherKhaDungResponse {
    private Integer idVoucher;
    private String maVoucher;
    private String tenVoucher;
    private String loaiVoucher;
    private BigDecimal giaTriGiam;
    private BigDecimal giamToiDa;
    private BigDecimal dieuKien;
    private BigDecimal soTienGiam;
    private Integer soLuong;
    private Integer idKhachHang;
    private LocalDateTime ngayKetThuc;
}