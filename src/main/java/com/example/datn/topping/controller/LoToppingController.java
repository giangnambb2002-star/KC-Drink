package com.example.datn.topping.controller;

import com.example.datn.common.ApiResponse;
import com.example.datn.common.PageResponse;
import com.example.datn.topping.dto.LoToppingRequest;
import com.example.datn.topping.dto.LoToppingResponse;
import com.example.datn.topping.service.LoToppingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lo-topping")
@RequiredArgsConstructor
public class LoToppingController {

    private final LoToppingService service;

    @GetMapping
    public ApiResponse<PageResponse<LoToppingResponse>> getAll(
            @RequestParam(required = false) Integer idTopping,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idLoTopping") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return new ApiResponse<>(
                200, "Lấy danh sách lô Topping thành công",
                service.getAll(idTopping, trangThai, page, size, sortBy, direction)
        );
    }

    @PostMapping
    public ApiResponse<LoToppingResponse> create(
            @Valid @RequestBody LoToppingRequest request) {
        return new ApiResponse<>(
                201, "Nhập kho lô Topping thành công",
                service.create(request)
        );
    }

    // 🔒 KHÓA LÔ TOPPING
    @PutMapping("/{id}/lock")
    public ApiResponse<LoToppingResponse> lock(@PathVariable Integer id) {
        return new ApiResponse<>(
                200, "Khóa lô Topping thành công",
                service.lock(id)
        );
    }

    // 🔓 MỞ KHÓA LÔ TOPPING
    @PutMapping("/{id}/unlock")
    public ApiResponse<LoToppingResponse> unlock(@PathVariable Integer id) {
        return new ApiResponse<>(
                200, "Mở khóa lô Topping thành công",
                service.unlock(id)
        );
    }
}