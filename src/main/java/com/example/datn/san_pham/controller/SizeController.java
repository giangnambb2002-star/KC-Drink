package com.example.datn.san_pham.controller;

import com.example.datn.common.ApiResponse;
import com.example.datn.san_pham.dto.SizeRequest;
import com.example.datn.san_pham.dto.SizeResponse;
import com.example.datn.san_pham.service.SizeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/size")
@RequiredArgsConstructor
public class SizeController {

    private final SizeService service;

    @GetMapping
    public ApiResponse<List<SizeResponse>> getAll() {
        return new ApiResponse<>(
                200,
                "Lấy danh sách size thành công",
                service.getAll()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<SizeResponse> getById(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Lấy size thành công",
                service.getById(id)
        );
    }

    @PostMapping
    public ApiResponse<SizeResponse> create(
            @Valid @RequestBody SizeRequest request) {
        return new ApiResponse<>(
                201,
                "Thêm size thành công",
                service.create(request)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<SizeResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody SizeRequest request) {
        return new ApiResponse<>(
                200,
                "Cập nhật size thành công",
                service.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return new ApiResponse<>(
                200,
                "Xóa size thành công",
                null
        );
    }
}