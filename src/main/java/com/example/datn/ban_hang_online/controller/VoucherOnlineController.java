package com.example.datn.ban_hang_online.controller;

import com.example.datn.ban_hang_online.dto.checkout.VoucherOnlineResponse;
import com.example.datn.ban_hang_online.service.VoucherOnlineService;
import com.example.datn.common.ApiResponse;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/online/vouchers")
@RequiredArgsConstructor
public class VoucherOnlineController {

    private final VoucherOnlineService voucherOnlineService;

    @GetMapping
    public ApiResponse<List<VoucherOnlineResponse>>
    getVoucherKhaDung(
            @RequestParam BigDecimal tongTien,
            Authentication authentication
    ) {

        TaiKhoan taiKhoan =
                getTaiKhoan(authentication);

        return new ApiResponse<>(
                200,
                "Lấy danh sách voucher khả dụng thành công",
                voucherOnlineService
                        .getVoucherKhaDung(
                                tongTien,
                                taiKhoan
                        )
        );
    }

    private TaiKhoan getTaiKhoan(
            Authentication authentication
    ) {

        if (authentication == null
                || authentication.getPrincipal() == null) {
            throw new RuntimeException(
                    "Chưa đăng nhập"
            );
        }

        return (TaiKhoan)
                authentication.getPrincipal();
    }
}