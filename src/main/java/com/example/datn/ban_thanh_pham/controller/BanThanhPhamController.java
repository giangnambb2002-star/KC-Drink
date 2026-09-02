package com.example.datn.ban_thanh_pham.controller;

import com.example.datn.ban_thanh_pham.dto.BanThanhPhamRequest;
import com.example.datn.ban_thanh_pham.dto.BanThanhPhamResponse;
import com.example.datn.ban_thanh_pham.service.BanThanhPhamService;
import com.example.datn.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ban-thanh-pham")
@RequiredArgsConstructor
public class BanThanhPhamController {

    private final BanThanhPhamService service;

    @GetMapping
    public ApiResponse<List<BanThanhPhamResponse>> getAll() {
        return new ApiResponse<>(200, "Lấy danh sách bán thành phẩm thành công", service.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<BanThanhPhamResponse> getById(@PathVariable Integer id) {
        return new ApiResponse<>(200, "Lấy bán thành phẩm thành công", service.getById(id));
    }

    @PostMapping
    public ApiResponse<BanThanhPhamResponse> create(@Valid @RequestBody BanThanhPhamRequest request) {
        return new ApiResponse<>(201, "Thêm bán thành phẩm thành công", service.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<BanThanhPhamResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody BanThanhPhamRequest request) {
        return new ApiResponse<>(200, "Cập nhật bán thành phẩm thành công", service.update(id, request));
    }

    @PatchMapping("/{id}/lock")
    public ApiResponse<BanThanhPhamResponse> lock(@PathVariable Integer id) {
        return new ApiResponse<>(200, "Khóa bán thành phẩm thành công", service.lock(id));
    }

    @PatchMapping("/{id}/unlock")
    public ApiResponse<BanThanhPhamResponse> unlock(@PathVariable Integer id) {
        return new ApiResponse<>(200, "Mở khóa bán thành phẩm thành công", service.unlock(id));
    }
}