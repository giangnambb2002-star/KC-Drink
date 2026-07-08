package com.example.datn.nguyen_lieu.controller;

import com.example.datn.common.ApiResponse;
import com.example.datn.common.PageResponse;
import com.example.datn.nguyen_lieu.dto.NguyenLieuRequest;
import com.example.datn.nguyen_lieu.dto.NguyenLieuResponse;
import com.example.datn.nguyen_lieu.service.NguyenLieuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nguyen-lieu")
@RequiredArgsConstructor
public class NguyenLieuController {

    private final NguyenLieuService service;

    // Danh sách + tìm kiếm + phân trang + sắp xếp (Khớp 100% KhachHangController)
    @GetMapping
    public ApiResponse<PageResponse<NguyenLieuResponse>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idNguyenLieu") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return new ApiResponse<>(
                200,
                "Lấy danh sách nguyên liệu thành công",
                service.getAll(keyword, trangThai, page, size, sortBy, direction)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<NguyenLieuResponse> getById(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Lấy chi tiết nguyên liệu thành công",
                service.getById(id)
        );
    }

    @PostMapping
    public ApiResponse<NguyenLieuResponse> create(
            @Valid @RequestBody NguyenLieuRequest request) {
        return new ApiResponse<>(
                201,
                "Thêm mới nguyên liệu thành công",
                service.create(request)
        );
    }
    // API Khóa nguyên liệu
    @PatchMapping("/{id}/lock")
    public ApiResponse<NguyenLieuResponse> lock(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Ngừng sử dụng nguyên liệu thành công",
                service.lock(id)
        );
    }

    // API Mở khóa nguyên liệu
    @PatchMapping("/{id}/unlock")
    public ApiResponse<NguyenLieuResponse> unlock(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Mở lại nguyên liệu thành công",
                service.unlock(id)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<NguyenLieuResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody NguyenLieuRequest request) {
        return new ApiResponse<>(
                200,
                "Cập nhật nguyên liệu thành công",
                service.update(id, request)
        );
    }
}