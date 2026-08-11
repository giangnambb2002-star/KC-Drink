package com.example.datn.payos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayOSCreateResponse {

    private Long orderCode;

    private Long amount;

    private String description;

    private String checkoutUrl;

    private String qrCode;

    private String paymentLinkId;

    private String status;
}