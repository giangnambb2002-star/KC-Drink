package com.example.datn.khach_hang.controller;

import com.example.datn.common.ApiResponse;
import com.example.datn.common.PageResponse;
import com.example.datn.khach_hang.dto.KhachHangRequest;
import com.example.datn.khach_hang.dto.KhachHangResponse;
import com.example.datn.khach_hang.service.KhachHangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/khach-hang")
@RequiredArgsConstructor
public class KhachHangController {

    private final KhachHangService service;

    // Danh sách + tìm kiếm + phân trang + sắp xếp
    @GetMapping
    public ApiResponse<PageResponse<KhachHangResponse>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idKhachHang") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return new ApiResponse<>(
                200,
                "Lấy danh sách khách hàng thành công",
                service.getAll(keyword, page, size, sortBy, direction)
        );
    }
    // Lấy theo ID
    @GetMapping("/{id}")
    public ApiResponse<KhachHangResponse> getById(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Lấy khách hàng thành công",
                service.getById(id)
        );
    }
    // Thêm mới
    @PostMapping
    public ApiResponse<KhachHangResponse> create(
            @Valid @RequestBody KhachHangRequest request) {

        return new ApiResponse<>(
                201,
                "Thêm khách hàng thành công",
                service.create(request)
        );
    }
    // Cập nhật
    @PutMapping("/{id}")
    public ApiResponse<KhachHangResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody KhachHangRequest request) {

        return new ApiResponse<>(
                200,
                "Cập nhật khách hàng thành công",
                service.update(id, request)
        );
    }
    // Khóa
    @PatchMapping("/{id}/lock")
    public ApiResponse<KhachHangResponse> lock(@PathVariable Integer id) {

        return new ApiResponse<>(
                200,
                "Khóa khách hàng thành công",
                service.lock(id)
        );
    }
    // Mở khóa
    @PatchMapping("/{id}/unlock")
    public ApiResponse<KhachHangResponse> unlock(@PathVariable Integer id) {

        return new ApiResponse<>(
                200,
                "Mở khóa khách hàng thành công",
                service.unlock(id)
        );
    }
}