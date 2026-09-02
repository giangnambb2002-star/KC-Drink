package com.example.datn.ban_thanh_pham.controller;

import com.example.datn.ban_thanh_pham.dto.CongThucBanThanhPhamRequest;
import com.example.datn.ban_thanh_pham.dto.CongThucBanThanhPhamResponse;
import com.example.datn.ban_thanh_pham.service.CongThucBanThanhPhamService;
import com.example.datn.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cong-thuc-ban-thanh-pham")
@RequiredArgsConstructor
public class CongThucBanThanhPhamController {

    private final CongThucBanThanhPhamService service;

    @GetMapping("/ban-thanh-pham/{idBanThanhPham}")
    public ApiResponse<List<CongThucBanThanhPhamResponse>> getByBanThanhPham(
            @PathVariable Integer idBanThanhPham) {
        return new ApiResponse<>(
                200,
                "Lấy công thức bán thành phẩm thành công",
                service.getByBanThanhPham(idBanThanhPham)
        );
    }

    @PostMapping
    public ApiResponse<CongThucBanThanhPhamResponse> create(
            @Valid @RequestBody CongThucBanThanhPhamRequest request) {
        return new ApiResponse<>(
                201,
                "Thêm công thức bán thành phẩm thành công",
                service.create(request)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<CongThucBanThanhPhamResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody CongThucBanThanhPhamRequest request) {
        return new ApiResponse<>(
                200,
                "Cập nhật công thức bán thành phẩm thành công",
                service.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return new ApiResponse<>(
                200,
                "Xóa công thức bán thành phẩm thành công",
                null
        );
    }
}