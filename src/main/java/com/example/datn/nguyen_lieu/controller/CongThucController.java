package com.example.datn.nguyen_lieu.controller;

import com.example.datn.common.ApiResponse;
import com.example.datn.common.PageResponse;
import com.example.datn.nguyen_lieu.dto.CongThucSanPhamRequest;
import com.example.datn.nguyen_lieu.dto.CongThucToppingRequest;
import com.example.datn.nguyen_lieu.entity.CongThucSanPham;
import com.example.datn.nguyen_lieu.entity.CongThucTopping;
import com.example.datn.nguyen_lieu.service.CongThucService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cong-thuc")
@RequiredArgsConstructor
public class CongThucController {

    private final CongThucService service;

    @PostMapping("/san-pham")
    public ApiResponse<CongThucSanPham> createCtsp(@Valid @RequestBody CongThucSanPhamRequest request) {
        return new ApiResponse<>(201, "Cài đặt công thức sản phẩm thành công", service.createCtsp(request));
    }

    @GetMapping("/san-pham")
    public ApiResponse<PageResponse<CongThucSanPham>> getCtsp(
            @RequestParam Integer idSanPham,
            @RequestParam Integer idSize,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idCtsp") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return new ApiResponse<>(
                200, "Lấy công thức sản phẩm thành công",
                service.getBySanPhamAndSize(idSanPham, idSize, page, size, sortBy, direction)
        );
    }

    @PostMapping("/topping")
    public ApiResponse<CongThucTopping> createCtt(@Valid @RequestBody CongThucToppingRequest request) {
        return new ApiResponse<>(201, "Cài đặt công thức topping thành công", service.createCtt(request));
    }

    @GetMapping("/topping/{idTopping}")
    public ApiResponse<PageResponse<CongThucTopping>> getCtt(
            @PathVariable Integer idTopping,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idCtt") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return new ApiResponse<>(
                200, "Lấy công thức topping thành công",
                service.getByTopping(idTopping, page, size, sortBy, direction)
        );
    }
}