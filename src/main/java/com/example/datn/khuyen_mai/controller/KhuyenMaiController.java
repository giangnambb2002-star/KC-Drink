package com.example.datn.khuyen_mai.controller;

import com.example.datn.common.ApiResponse;
import com.example.datn.common.PageResponse;
import com.example.datn.khuyen_mai.dto.*;
import com.example.datn.khuyen_mai.service.KhuyenMaiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/khuyen-mai")
@RequiredArgsConstructor
public class KhuyenMaiController {

    private final KhuyenMaiService service;

    @GetMapping
    public ApiResponse<PageResponse<KhuyenMaiResponse>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idKm") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return new ApiResponse<>(
                200,
                "Lấy danh sách khuyến mãi thành công",
                service.getAll(
                        keyword,
                        trangThai,
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<KhuyenMaiResponse> getById(
            @PathVariable Integer id
    ) {
        return new ApiResponse<>(
                200,
                "Lấy thông tin khuyến mãi thành công",
                service.getById(id)
        );
    }

    @PostMapping
    public ApiResponse<KhuyenMaiResponse> create(
            @Valid @RequestBody KhuyenMaiRequest request
    ) {
        return new ApiResponse<>(
                201,
                "Thêm chương trình khuyến mãi thành công",
                service.create(request)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<KhuyenMaiResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody KhuyenMaiRequest request
    ) {
        return new ApiResponse<>(
                200,
                "Cập nhật chương trình khuyến mãi thành công",
                service.update(id, request)
        );
    }

    @PatchMapping("/{id}/lock")
    public ApiResponse<KhuyenMaiResponse> lock(
            @PathVariable Integer id
    ) {
        return new ApiResponse<>(
                200,
                "Ngừng chương trình khuyến mãi thành công",
                service.lock(id)
        );
    }

    @PatchMapping("/{id}/unlock")
    public ApiResponse<KhuyenMaiResponse> unlock(
            @PathVariable Integer id
    ) {
        return new ApiResponse<>(
                200,
                "Mở chương trình khuyến mãi thành công",
                service.unlock(id)
        );
    }

    @GetMapping("/san-pham/{idSanPham}/gia")
    public ApiResponse<KetQuaKhuyenMaiResponse>
    getGiaKhuyenMai(
            @PathVariable Integer idSanPham
    ) {
        return new ApiResponse<>(
                200,
                "Tính giá khuyến mãi thành công",
                service.tinhKhuyenMaiChoSanPham(idSanPham)
        );
    }
}