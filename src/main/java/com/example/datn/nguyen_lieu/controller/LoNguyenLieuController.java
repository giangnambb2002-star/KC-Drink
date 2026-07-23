package com.example.datn.nguyen_lieu.controller;

import com.example.datn.common.ApiResponse;
import com.example.datn.common.PageResponse;
import com.example.datn.nguyen_lieu.dto.LoNguyenLieuRequest;
import com.example.datn.nguyen_lieu.dto.LoNguyenLieuResponse;
import com.example.datn.nguyen_lieu.service.LoNguyenLieuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lo-nguyen-lieu")
@RequiredArgsConstructor
public class LoNguyenLieuController {

    private final LoNguyenLieuService service;

    @GetMapping
    public ApiResponse<PageResponse<LoNguyenLieuResponse>> getAll(
            @RequestParam(required = false) Integer idNguyenLieu,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idLo") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return new ApiResponse<>(
                200, "Lấy danh sách lô nguyên liệu thành công",
                service.getAll(idNguyenLieu, trangThai, page, size, sortBy, direction)
        );
    }

    @PostMapping
    public ApiResponse<LoNguyenLieuResponse> create(
            @Valid @RequestBody LoNguyenLieuRequest request) {
        return new ApiResponse<>(
                201, "Nhập kho lô nguyên liệu thành công",
                service.create(request)
        );
    }

    @PutMapping("/{id}/lock")
    public ApiResponse<LoNguyenLieuResponse> lock(@PathVariable Integer id) {
        return new ApiResponse<>(
                200, "Khóa lô nguyên liệu thành công",
                service.lock(id)
        );
    }

    // 🔓 Mở khóa lô nguyên liệu
    @PutMapping("/{id}/unlock")
    public ApiResponse<LoNguyenLieuResponse> unlock(@PathVariable Integer id) {
        return new ApiResponse<>(
                200, "Mở khóa lô nguyên liệu thành công",
                service.unlock(id)
        );
    }
}