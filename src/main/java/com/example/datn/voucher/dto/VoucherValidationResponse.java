package com.example.datn.voucher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherValidationResponse {
    private boolean hopLe;
    private BigDecimal soTienGiam;
    private String thongBao;
}