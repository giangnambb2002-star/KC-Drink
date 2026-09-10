package com.example.datn.ban_hang_online.controller;

import com.example.datn.ban_hang_online.dto.checkout.XemTruocGioHangRequest;
import com.example.datn.ban_hang_online.dto.checkout.XemTruocGioHangResponse;
import com.example.datn.ban_hang_online.service.GioHangOnlineService;
import com.example.datn.common.ApiResponse;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/online/checkout")
@RequiredArgsConstructor
public class GioHangOnlineController {

    private final GioHangOnlineService gioHangOnlineService;

    @PostMapping("/preview")
    public ApiResponse<XemTruocGioHangResponse> preview(
            @RequestBody XemTruocGioHangRequest request,
            Authentication authentication
    ) {

        if (authentication == null
                || authentication.getPrincipal() == null) {
            throw new RuntimeException("Chưa đăng nhập");
        }

        TaiKhoan taiKhoan =
                (TaiKhoan) authentication.getPrincipal();

        XemTruocGioHangResponse result =
                gioHangOnlineService.preview(
                        request,
                        taiKhoan
                );

        return new ApiResponse<>(
                200,
                "Tính giá giỏ hàng thành công",
                result
        );
    }
}