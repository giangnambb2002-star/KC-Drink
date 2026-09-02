package com.example.datn.san_pham.controller;

import com.example.datn.common.ApiResponse;
import com.example.datn.san_pham.dto.SanPhamSizeRequest;
import com.example.datn.san_pham.dto.SanPhamSizeResponse;
import com.example.datn.san_pham.service.SanPhamSizeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/san-pham-size")
@RequiredArgsConstructor
public class SanPhamSizeController {

    private final SanPhamSizeService service;

    @GetMapping("/san-pham/{idSanPham}")
    public ApiResponse<List<SanPhamSizeResponse>> getBySanPham(
            @PathVariable Integer idSanPham) {
        return new ApiResponse<>(
                200,
                "Lấy danh sách size của sản phẩm thành công",
                service.getBySanPham(idSanPham)
        );
    }

    @PostMapping
    public ApiResponse<SanPhamSizeResponse> create(
            @Valid @RequestBody SanPhamSizeRequest request) {
        return new ApiResponse<>(
                201,
                "Thêm size cho sản phẩm thành công",
                service.create(request)
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return new ApiResponse<>(
                200,
                "Xóa size khỏi sản phẩm thành công",
                null
        );
    }
    @PutMapping("/{id}")
    public ApiResponse<SanPhamSizeResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody SanPhamSizeRequest request) {

        return new ApiResponse<>(
                200,
                "Cập nhật phụ thu size thành công",
                service.update(id, request)
        );
    }
}