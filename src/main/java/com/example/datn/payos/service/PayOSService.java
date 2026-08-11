package com.example.datn.payos.service;

import com.example.datn.payos.dto.PayOSCreateRequest;
import com.example.datn.payos.dto.PayOSCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;

@Service
@RequiredArgsConstructor
public class PayOSService {

    private final PayOS payOS;

    public PayOSCreateResponse createPayment(
            PayOSCreateRequest request
    ) {

        try {

            long orderCode =
                    System.currentTimeMillis() / 1000;

            CreatePaymentLinkRequest paymentRequest =
                    CreatePaymentLinkRequest.builder()
                            .orderCode(orderCode)
                            .amount(request.getAmount())
                            .description(request.getDescription())
                            .cancelUrl(
                                    "http://localhost:5173/payos/cancel"
                            )
                            .returnUrl(
                                    "http://localhost:5173/payos/success"
                            )
                            .build();

            var paymentLink =
                    payOS.paymentRequests()
                            .create(paymentRequest);

            return new PayOSCreateResponse(
                    orderCode,
                    request.getAmount(),
                    request.getDescription(),
                    paymentLink.getCheckoutUrl(),
                    paymentLink.getQrCode(),
                    paymentLink.getPaymentLinkId(),
                    paymentLink.getStatus().toString()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Không thể tạo link thanh toán payOS: "
                            + e.getMessage()
            );
        }
    }
    public Object getPaymentInfo(Long orderCode) {
        try {
            return payOS.paymentRequests().get(orderCode);
        } catch (Exception e) {
            throw new RuntimeException("Không thể lấy trạng thái thanh toán payOS: " + e.getMessage());
        }
    }
}