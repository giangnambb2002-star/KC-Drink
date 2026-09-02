package com.example.datn.ban_thanh_pham.controller;

import com.example.datn.ban_thanh_pham.dto.MePhaCheResponse;
import com.example.datn.ban_thanh_pham.dto.TaoMePhaCheRequest;
import com.example.datn.ban_thanh_pham.service.MePhaCheService;
import com.example.datn.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me-pha-che")
@RequiredArgsConstructor
public class MePhaCheController {

    private final MePhaCheService service;

    @GetMapping
    public ApiResponse<List<MePhaCheResponse>> getAll() {
        return new ApiResponse<>(
                200,
                "Lấy danh sách mẻ pha thành công",
                service.getAll()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<MePhaCheResponse> getById(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Lấy mẻ pha thành công",
                service.getById(id)
        );
    }

    @PostMapping
    public ApiResponse<MePhaCheResponse> create(
            @Valid @RequestBody TaoMePhaCheRequest request) {
        return new ApiResponse<>(
                201,
                "Tạo mẻ pha thành công",
                service.create(request)
        );
    }
}