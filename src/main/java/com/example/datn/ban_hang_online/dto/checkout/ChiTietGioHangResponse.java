package com.example.datn.ban_hang_online.dto.checkout;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietGioHangResponse {

    private Integer idSanPham;

    private String tenSanPham;

    private Integer idSize;

    private String tenSize;

    private Integer mucDuong;

    private Integer mucDa;

    private Integer soLuong;

    private String ghiChu;

    // Giá gốc riêng của sản phẩm, chưa size
    private BigDecimal giaSanPhamGoc;

    // Phụ thu size / 1 ly
    private BigDecimal phuThuSize;

    private Integer idKm;

    private String tenKhuyenMai;

    /**
     * Tiền giảm CTKM của toàn bộ số lượng ly trong dòng.
     */
    private BigDecimal tienGiamKhuyenMai;

    /**
     * Đơn giá sau CTKM + phụ thu size của 1 ly.
     */
    private BigDecimal donGiaSauKhuyenMai;

    /**
     * (giá sản phẩm gốc + phụ thu size) * số lượng.
     */
    private BigDecimal tienSanPhamTruocKhuyenMai;

    /**
     * (giá sản phẩm sau KM + phụ thu size) * số lượng.
     */
    private BigDecimal tienSanPhamSauKhuyenMai;

    private List<ToppingGioHangResponse> toppings;

    /**
     * Tổng topping của cả dòng.
     * Không nhân thêm theo số lượng ly.
     */
    private BigDecimal tienTopping;

    /**
     * tienSanPhamSauKhuyenMai + tienTopping
     */
    private BigDecimal thanhTienDong;
}