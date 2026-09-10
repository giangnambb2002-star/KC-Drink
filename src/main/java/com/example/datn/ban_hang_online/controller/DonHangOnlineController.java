package com.example.datn.ban_hang_online.controller;
import com.example.datn.ban_hang_online.dto.checkout.TaoDonHangOnlineRequest;
import com.example.datn.ban_hang_online.dto.checkout.TaoDonHangOnlineResponse;
import com.example.datn.ban_hang_online.dto.checkout.TrangThaiThanhToanOnlineResponse;
import com.example.datn.ban_hang_online.service.DonHangOnlineService;
import com.example.datn.ban_hang_online.service.ThanhToanOnlineService;
import com.example.datn.common.ApiResponse;
import com.example.datn.ban_hang_online.dto.don_hang.ChiTietDonHangOnlineResponse;
import com.example.datn.ban_hang_online.dto.don_hang.DonHangCuaToiResponse;
import com.example.datn.common.PageResponse;
import com.example.datn.payos.dto.PayOSCreateResponse;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/online/orders")
@RequiredArgsConstructor
public class DonHangOnlineController {
    private final DonHangOnlineService donHangOnlineService;
    private final ThanhToanOnlineService thanhToanOnlineService;
    @PostMapping
    public ApiResponse<TaoDonHangOnlineResponse> taoDonHang(
            @RequestBody TaoDonHangOnlineRequest request,
            Authentication authentication
    ) {
        if (authentication == null
                || authentication.getPrincipal() == null) {
            throw new RuntimeException(
                    "Chưa đăng nhập"
            );
        }
        TaiKhoan taiKhoan =
                (TaiKhoan) authentication.getPrincipal();
        try {
            TaoDonHangOnlineResponse result =
                    donHangOnlineService.taoDonHang(
                            request,
                            taiKhoan
                    );
            String message =
                    Boolean.TRUE.equals(
                            result.getTaoMoi()
                    )
                            ? "Tạo đơn hàng online thành công"
                            : "Đơn hàng đã được tạo trước đó";
            return new ApiResponse<>(
                    200,
                    message,
                    result
            );
        } catch (DataIntegrityViolationException ex) {
            /*
             * Trường hợp hiếm:
             *
             * 2 request cùng UUID chạy đúng một lúc.
             *
             * Unique index DB chỉ cho 1 hóa đơn được tạo.
             * Request còn lại tìm lại đơn đó và trả về.
             */
            TaoDonHangOnlineResponse existing =
                    donHangOnlineService
                            .timLaiDonHangTheoClientRequestId(
                                    request.getClientRequestId(),
                                    taiKhoan
                            );
            if (existing != null) {
                return new ApiResponse<>(
                        200,
                        "Đơn hàng đã được tạo trước đó",
                        existing
                );
            }
            throw ex;
        }
    }
    @PostMapping("/{idHoaDon}/payment")
    public ApiResponse<PayOSCreateResponse> taoThanhToan(
            @PathVariable Integer idHoaDon,
            Authentication authentication
    ) {
        TaiKhoan taiKhoan =
                getTaiKhoan(authentication);
        PayOSCreateResponse result =
                thanhToanOnlineService
                        .taoThanhToan(
                                idHoaDon,
                                taiKhoan
                        );
        return new ApiResponse<>(
                200,
                "Tạo thanh toán PayOS thành công",
                result
        );
    }
    @GetMapping("/{idHoaDon}/payment-status")
    public ApiResponse<TrangThaiThanhToanOnlineResponse>
    kiemTraThanhToan(
            @PathVariable Integer idHoaDon,
            Authentication authentication
    ) {
        TaiKhoan taiKhoan =
                getTaiKhoan(authentication);
        TrangThaiThanhToanOnlineResponse result =
                thanhToanOnlineService
                        .kiemTraTrangThaiThanhToan(
                                idHoaDon,
                                taiKhoan
                        );
        return new ApiResponse<>(
                200,
                Boolean.TRUE.equals(
                        result.getDaThanhToan()
                )
                        ? "Đơn hàng đã thanh toán"
                        : "Lấy trạng thái thanh toán thành công",
                result
        );
    }
    @PostMapping("/{idHoaDon}/payment/cancel")
    public ApiResponse<TrangThaiThanhToanOnlineResponse>
    huyThanhToan(
            @PathVariable Integer idHoaDon,
            Authentication authentication
    ) {
        TaiKhoan taiKhoan =
                getTaiKhoan(authentication);
        TrangThaiThanhToanOnlineResponse result =
                thanhToanOnlineService
                        .huyThanhToan(
                                idHoaDon,
                                taiKhoan
                        );
        return new ApiResponse<>(
                200,
                Boolean.TRUE.equals(
                        result.getDaThanhToan()
                )
                        ? "Giao dịch đã thanh toán nên không thể hủy"
                        : "Hủy thanh toán PayOS thành công",
                result
        );
    }
    private TaiKhoan getTaiKhoan(
            Authentication authentication
    ) {
        if (authentication == null
                || authentication.getPrincipal() == null) {
            throw new RuntimeException(
                    "Chưa đăng nhập"
            );
        }
        return (TaiKhoan)
                authentication.getPrincipal();
    }
    @GetMapping
    public ApiResponse<PageResponse<DonHangCuaToiResponse>>
    getDonHangCuaToi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        TaiKhoan taiKhoan =
                getTaiKhoan(authentication);
        return new ApiResponse<>(
                200,
                "Lấy danh sách đơn hàng thành công",
                donHangOnlineService
                        .getDonHangCuaToi(
                                taiKhoan,
                                page,
                                size
                        )
        );
    }
    @GetMapping("/{idHoaDon}")
    public ApiResponse<ChiTietDonHangOnlineResponse>
    getChiTietDonHang(
            @PathVariable Integer idHoaDon,
            Authentication authentication
    ) {
        TaiKhoan taiKhoan =
                getTaiKhoan(authentication);
        return new ApiResponse<>(
                200,
                "Lấy chi tiết đơn hàng thành công",
                donHangOnlineService
                        .getChiTietDonHang(
                                idHoaDon,
                                taiKhoan
                        )
        );
    }
    @PostMapping("/{idHoaDon}/cancel")
    public ApiResponse<ChiTietDonHangOnlineResponse>
    huyDonHang(
            @PathVariable Integer idHoaDon,
            Authentication authentication
    ) {
        TaiKhoan taiKhoan =
                getTaiKhoan(authentication);
        return new ApiResponse<>(
                200,
                "Hủy đơn hàng thành công",
                donHangOnlineService
                        .huyDonHang(
                                idHoaDon,
                                taiKhoan
                        )
        );
    }
}