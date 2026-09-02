package com.example.datn.san_pham.controller;

import com.example.datn.common.ApiResponse;
import com.example.datn.common.PageResponse;
import com.example.datn.san_pham.dto.SanPhamResponse;
import com.example.datn.san_pham.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.example.datn.san_pham.dto.SanPhamRequest;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/san-pham")
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService service;


    @GetMapping("/manage")
    public ApiResponse<PageResponse<SanPhamResponse>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idSanPham") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return new ApiResponse<>(
                200,
                "Lấy danh sách sản phẩm thành công",
                service.getAll(keyword, trangThai, page, size, sortBy, direction)
        );
    }

    @PostMapping
    public ApiResponse<SanPhamResponse> create(
            @Valid @RequestBody SanPhamRequest request) {
        return new ApiResponse<>(
                201,
                "Thêm sản phẩm thành công",
                service.create(request)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<SanPhamResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody SanPhamRequest request) {
        return new ApiResponse<>(
                200,
                "Cập nhật sản phẩm thành công",
                service.update(id, request)
        );
    }

    @PatchMapping("/{id}/lock")
    public ApiResponse<SanPhamResponse> lock(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Ngừng bán sản phẩm thành công",
                service.lock(id)
        );
    }

    @PatchMapping("/{id}/unlock")
    public ApiResponse<SanPhamResponse> unlock(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Mở bán sản phẩm thành công",
                service.unlock(id)
        );
    }
    @GetMapping
    public ApiResponse<PageResponse<SanPhamResponse>> getSanPhamDangBan(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "idSanPham") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return new ApiResponse<>(
                200,
                "Lấy danh sách sản phẩm thành công",
                service.getSanPhamDangBan(page, size, sortBy, direction)
        );
    }
    @GetMapping("/{id}")
    public ApiResponse<SanPhamResponse> getById(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Lấy sản phẩm thành công",
                service.getById(id)
        );
    }
}