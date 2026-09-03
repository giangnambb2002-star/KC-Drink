package com.example.datn.payos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayOSPaymentStatusResponse {
    private Long orderCode;
    private Long amount;
    private String status;
}