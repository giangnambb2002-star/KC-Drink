package com.example.datn.payos.controller;

import com.example.datn.common.ApiResponse;
import com.example.datn.payos.dto.PayOSCreateRequest;
import com.example.datn.payos.dto.PayOSCreateResponse;
import com.example.datn.payos.service.PayOSService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payos")
@RequiredArgsConstructor
public class PayOSController {

    private final PayOSService service;

    @PostMapping("/create-payment")
    public ApiResponse<PayOSCreateResponse> createPayment(
            @Valid @RequestBody PayOSCreateRequest request
    ) {

        return new ApiResponse<>(
                200,
                "Tạo link thanh toán thành công",
                service.createPayment(request)
        );
    }
    @GetMapping("/{orderCode}")
    public ApiResponse<Object> getPaymentInfo(@PathVariable Long orderCode) {
        return new ApiResponse<>(200, "Lấy trạng thái thanh toán thành công", service.getPaymentInfo(orderCode));
    }
}