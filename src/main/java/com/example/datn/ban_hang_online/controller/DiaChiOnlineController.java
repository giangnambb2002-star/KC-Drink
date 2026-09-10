package com.example.datn.ban_hang_online.controller;

import com.example.datn.ban_hang_online.dto.address.DiaChiOnlineRequest;
import com.example.datn.ban_hang_online.service.DiaChiOnlineService;
import com.example.datn.common.ApiResponse;
import com.example.datn.dia_chi.dto.DiaChiResponse;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/online/dia-chi")
@RequiredArgsConstructor
public class DiaChiOnlineController {

    private final DiaChiOnlineService diaChiOnlineService;

    @GetMapping
    public ApiResponse<List<DiaChiResponse>> layDiaChiCuaToi(
            Authentication authentication
    ) {

        TaiKhoan taiKhoan =
                getTaiKhoan(authentication);

        return new ApiResponse<>(
                200,
                "Lấy danh sách địa chỉ nhận hàng thành công",
                diaChiOnlineService
                        .getMyAddresses(taiKhoan)
        );
    }

    @PostMapping
    public ApiResponse<DiaChiResponse> themDiaChi(
            Authentication authentication,
            @Valid
            @RequestBody
                    DiaChiOnlineRequest request
    ) {

        TaiKhoan taiKhoan =
                getTaiKhoan(authentication);

        return new ApiResponse<>(
                201,
                "Thêm địa chỉ nhận hàng thành công",
                diaChiOnlineService
                        .createMyAddress(
                                taiKhoan,
                                request
                        )
        );
    }

    @PutMapping("/{idDiaChi}")
    public ApiResponse<DiaChiResponse> capNhatDiaChi(
            Authentication authentication,
            @PathVariable Integer idDiaChi,
            @Valid
            @RequestBody
                    DiaChiOnlineRequest request
    ) {

        TaiKhoan taiKhoan =
                getTaiKhoan(authentication);

        return new ApiResponse<>(
                200,
                "Cập nhật địa chỉ nhận hàng thành công",
                diaChiOnlineService
                        .updateMyAddress(
                                taiKhoan,
                                idDiaChi,
                                request
                        )
        );
    }

    @PatchMapping("/{idDiaChi}/default")
    public ApiResponse<DiaChiResponse> datMacDinh(
            Authentication authentication,
            @PathVariable Integer idDiaChi
    ) {

        TaiKhoan taiKhoan =
                getTaiKhoan(authentication);

        return new ApiResponse<>(
                200,
                "Đặt địa chỉ mặc định thành công",
                diaChiOnlineService
                        .setMyDefaultAddress(
                                taiKhoan,
                                idDiaChi
                        )
        );
    }

    private TaiKhoan getTaiKhoan(
            Authentication authentication
    ) {

        if (authentication == null
                || authentication.getPrincipal() == null
                || !(authentication.getPrincipal()
                instanceof TaiKhoan)) {

            throw new RuntimeException(
                    "Chưa đăng nhập"
            );
        }

        return (TaiKhoan)
                authentication.getPrincipal();
    }
}