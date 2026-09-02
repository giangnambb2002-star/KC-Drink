package com.example.datn.ban_thanh_pham.controller;

import com.example.datn.ban_thanh_pham.dto.CongThucSanPhamBtpRequest;
import com.example.datn.ban_thanh_pham.dto.CongThucSanPhamBtpResponse;
import com.example.datn.ban_thanh_pham.service.CongThucSanPhamBtpService;
import com.example.datn.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cong-thuc-san-pham-btp")
@RequiredArgsConstructor
public class CongThucSanPhamBtpController {

    private final CongThucSanPhamBtpService service;

    @GetMapping
    public ApiResponse<List<CongThucSanPhamBtpResponse>> getBySanPhamAndSize(
            @RequestParam Integer idSanPham,
            @RequestParam Integer idSize) {

        return new ApiResponse<>(
                200,
                "Lấy công thức sản phẩm bán thành phẩm thành công",
                service.getBySanPhamAndSize(idSanPham, idSize)
        );
    }

    @PostMapping
    public ApiResponse<CongThucSanPhamBtpResponse> create(
            @Valid @RequestBody CongThucSanPhamBtpRequest request) {

        return new ApiResponse<>(
                201,
                "Thêm công thức sản phẩm bán thành phẩm thành công",
                service.create(request)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<CongThucSanPhamBtpResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody CongThucSanPhamBtpRequest request) {

        return new ApiResponse<>(
                200,
                "Cập nhật công thức sản phẩm bán thành phẩm thành công",
                service.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        service.delete(id);

        return new ApiResponse<>(
                200,
                "Xóa công thức sản phẩm bán thành phẩm thành công",
                null
        );
    }
}