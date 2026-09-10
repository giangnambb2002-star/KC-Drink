package com.example.datn.ban_hang_online.controller;

import com.example.datn.ban_hang_online.dto.ChiTietSanPhamOnlineResponse;
import com.example.datn.ban_hang_online.service.SanPhamOnlineService;
import com.example.datn.common.ApiResponse;
import com.example.datn.common.PageResponse;
import com.example.datn.san_pham.dto.SanPhamResponse;
import com.example.datn.san_pham.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/online/products")
@RequiredArgsConstructor
public class SanPhamOnlineController {

    private final SanPhamService sanPhamService;

    private final SanPhamOnlineService sanPhamOnlineService;

    /**
     * Danh sách sản phẩm đang bán.
     */
    @GetMapping
    public ApiResponse<PageResponse<SanPhamResponse>> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        int pageHopLe = Math.max(page, 0);

        int sizeHopLe = Math.min(
                Math.max(size, 1),
                50
        );

        PageResponse<SanPhamResponse> result =
                sanPhamService.getSanPhamDangBan(
                        keyword,
                        pageHopLe,
                        sizeHopLe,
                        "idSanPham",
                        "asc"
                );

        return new ApiResponse<>(
                200,
                "Lấy danh sách sản phẩm online thành công",
                result
        );
    }

    /**
     * Chi tiết sản phẩm để khách chọn:
     * - sản phẩm + CTKM
     * - size
     * - topping còn hàng
     */
    @GetMapping("/{idSanPham}")
    public ApiResponse<ChiTietSanPhamOnlineResponse> getProductDetail(
            @PathVariable Integer idSanPham
    ) {
        ChiTietSanPhamOnlineResponse result =
                sanPhamOnlineService.getProductDetail(idSanPham);

        return new ApiResponse<>(
                200,
                "Lấy chi tiết sản phẩm online thành công",
                result
        );
    }
}