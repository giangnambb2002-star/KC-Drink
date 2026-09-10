package com.example.datn.ban_hang_online.service;
import com.example.datn.ban_hang_online.dto.ChiTietSanPhamOnlineResponse;
import com.example.datn.common.PageResponse;
import com.example.datn.san_pham.dto.SanPhamResponse;
import com.example.datn.san_pham.dto.SanPhamSizeResponse;
import com.example.datn.san_pham.service.SanPhamService;
import com.example.datn.san_pham.service.SanPhamSizeService;
import com.example.datn.topping.dto.ToppingResponse;
import com.example.datn.topping.service.ToppingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class SanPhamOnlineService {
    private final SanPhamService sanPhamService;
    private final SanPhamSizeService sanPhamSizeService;
    private final ToppingService toppingService;
    public ChiTietSanPhamOnlineResponse getProductDetail(Integer idSanPham) {
        // Reuse service sản phẩm hiện tại.
        // SanPhamResponse đã có giá gốc + CTKM tốt nhất.
        SanPhamResponse sanPham =
                sanPhamService.getById(idSanPham);
        // Online không cho xem sản phẩm đã khóa/ngừng bán.
        if (sanPham.getTrangThai() == null
                || sanPham.getTrangThai() != 1) {
            throw new RuntimeException(
                    "Sản phẩm hiện không còn được bán"
            );
        }
        // Reuse toàn bộ cấu hình size + phụ thu hiện tại.
        List<SanPhamSizeResponse> sizes =
                sanPhamSizeService.getBySanPham(idSanPham);
        /*
         * Topping hiện tại là dùng chung, không gắn riêng từng sản phẩm.
         *
         * ToppingService.toResponse() đã tính tongTonKho
         * dựa trên các lô topping còn hạn sử dụng.
         *
         * Online chỉ trả topping:
         * - đang hoạt động
         * - còn tồn kho
         */
        PageResponse<ToppingResponse> toppingPage =
                toppingService.getAll(
                        null,
                        1,
                        0,
                        1000,
                        "idTopping",
                        "asc"
                );
        List<ToppingResponse> toppings =
                toppingPage.getContent()
                        .stream()
                        .filter(topping ->
                                topping.getTongTonKho() != null
                                        && topping.getTongTonKho() > 0
                        )
                        .toList();
        return new ChiTietSanPhamOnlineResponse(
                sanPham,
                sizes,
                toppings
        );
    }
}