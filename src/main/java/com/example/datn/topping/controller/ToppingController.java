package com.example.datn.topping.controller;

import com.example.datn.common.ApiResponse;
import com.example.datn.common.PageResponse;
import com.example.datn.topping.dto.ToppingRequest;
import com.example.datn.topping.dto.ToppingResponse;
import com.example.datn.topping.service.ToppingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/topping")
@RequiredArgsConstructor
public class ToppingController {

    private final ToppingService service;

    @GetMapping
    public ApiResponse<PageResponse<ToppingResponse>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idTopping") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return new ApiResponse<>(
                200,
                "Lấy danh sách topping thành công",
                service.getAll(keyword, trangThai, page, size, sortBy, direction)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<ToppingResponse> getById(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Lấy topping thành công",
                service.getById(id)
        );
    }

    @PostMapping
    public ApiResponse<ToppingResponse> create(
            @Valid @RequestBody ToppingRequest request) {
        return new ApiResponse<>(
                201,
                "Thêm topping thành công",
                service.create(request)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<ToppingResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody ToppingRequest request) {
        return new ApiResponse<>(
                200,
                "Cập nhật topping thành công",
                service.update(id, request)
        );
    }

    @PatchMapping("/{id}/lock")
    public ApiResponse<ToppingResponse> lock(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Ngừng bán topping thành công",
                service.lock(id)
        );
    }

    @PatchMapping("/{id}/unlock")
    public ApiResponse<ToppingResponse> unlock(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Mở bán topping thành công",
                service.unlock(id)
        );
    }
}