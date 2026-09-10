package com.example.datn.ban_hang_online.dto.checkout;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class XemTruocGioHangResponse {

    private List<ChiTietGioHangResponse> items;

    /*
     * Tiền hàng + size + topping
     * trước khi trừ CTKM sản phẩm.
     */
    private BigDecimal tamTinhTruocKhuyenMai;

    /*
     * Tổng tiền được giảm bởi CTKM sản phẩm.
     */
    private BigDecimal tienGiamKhuyenMai;

    /*
     * Tiền hàng sau CTKM.
     * Voucher sẽ tính trên số tiền này.
     */
    private BigDecimal tamTinhSauKhuyenMai;

    /*
     * Voucher đang được áp dụng.
     * null nếu khách không chọn voucher.
     */
    private Integer idVoucher;

    private String maVoucher;

    private String tenVoucher;

    private String loaiVoucher;

    /*
     * Tiền thực tế được giảm bởi voucher.
     */
    private BigDecimal tienGiamVoucher;

    /*
     * Phí GHN backend tự tính.
     */
    private BigDecimal phiVanChuyen;

    /*
     * tamTinhSauKhuyenMai
     * - tienGiamVoucher
     * + phiVanChuyen
     */
    private BigDecimal tongThanhToan;
}