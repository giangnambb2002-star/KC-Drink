package com.example.datn.dia_chi.controller;

import com.example.datn.common.ApiResponse;
import com.example.datn.common.PageResponse;
import com.example.datn.dia_chi.dto.DiaChiRequest;
import com.example.datn.dia_chi.dto.DiaChiResponse;
import com.example.datn.dia_chi.service.DiaChiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dia-chi")
@RequiredArgsConstructor
public class DiaChiController {

    private final DiaChiService service;

    @GetMapping
    public ApiResponse<PageResponse<DiaChiResponse>> getAll(

            @RequestParam Integer idKhachHang,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "idDiaChi") String sortBy,

            @RequestParam(defaultValue = "asc") String direction
    ) {

        return new ApiResponse<>(
                200,
                "Lấy danh sách địa chỉ thành công",
                service.getAll(
                        idKhachHang,
                        page,
                        size,
                        sortBy,
                        direction
                )
        );

    }
    @GetMapping("/{id}")
    public ApiResponse<DiaChiResponse> getById(
            @PathVariable Integer id
    ) {

        return new ApiResponse<>(
                200,
                "Lấy địa chỉ thành công",
                service.getById(id)
        );

    }

    @PostMapping
    public ApiResponse<DiaChiResponse> create(
            @Valid @RequestBody DiaChiRequest request
    ) {

        return new ApiResponse<>(
                201,
                "Thêm địa chỉ thành công",
                service.create(request)
        );

    }

    @PutMapping("/{id}")
    public ApiResponse<DiaChiResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody DiaChiRequest request
    ) {

        return new ApiResponse<>(
                200,
                "Cập nhật địa chỉ thành công",
                service.update(id, request)
        );

    }

    @PatchMapping("/{id}/lock")
    public ApiResponse<DiaChiResponse> lock(
            @PathVariable Integer id
    ) {

        return new ApiResponse<>(
                200,
                "Khóa địa chỉ thành công",
                service.lock(id)
        );

    }

    @PatchMapping("/{id}/unlock")
    public ApiResponse<DiaChiResponse> unlock(
            @PathVariable Integer id
    ) {

        return new ApiResponse<>(
                200,
                "Mở khóa địa chỉ thành công",
                service.unlock(id)
        );

    }
    @PatchMapping("/{id}/default")
    public ApiResponse<DiaChiResponse> setDefault(
            @PathVariable Integer id
    ) {

        return new ApiResponse<>(
                200,
                "Đặt địa chỉ mặc định thành công",
                service.setDefault(id)
        );

    }
}