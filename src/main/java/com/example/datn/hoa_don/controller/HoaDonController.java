package com.example.datn.hoa_don.controller;
import com.example.datn.common.ApiResponse;
import com.example.datn.hoa_don.dto.ApDungVoucherRequest;
import com.example.datn.hoa_don.dto.CapNhatKhachHangHoaDonRequest;
import com.example.datn.hoa_don.dto.CapNhatSoLuongRequest;
import com.example.datn.hoa_don.dto.CapNhatToppingRequest;
import com.example.datn.hoa_don.dto.HoaDonResponse;
import com.example.datn.hoa_don.dto.TaoHoaDonOfflineRequest;
import com.example.datn.hoa_don.dto.ThanhToanHoaDonRequest;
import com.example.datn.hoa_don.dto.ThemMonRequest;
import com.example.datn.hoa_don.dto.ThemToppingRequest;
import com.example.datn.hoa_don.dto.VoucherKhaDungResponse;
import com.example.datn.hoa_don.service.HoaDonService;
import com.example.datn.payos.dto.PayOSCreateResponse;
import com.example.datn.payos.dto.PayOSPaymentStatusResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/hoa-don")
@RequiredArgsConstructor
public class HoaDonController {
    private final HoaDonService service;
    @GetMapping("/{id}")
    public ApiResponse<HoaDonResponse> getById(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Lấy hóa đơn thành công",
                service.getById(id)
        );
    }
    @PostMapping("/offline")
    public ApiResponse<HoaDonResponse> taoHoaDonOffline(
            @RequestBody TaoHoaDonOfflineRequest request) {
        return new ApiResponse<>(
                201,
                "Tạo hóa đơn tại quầy thành công",
                service.taoHoaDonOffline(request)
        );
    }
    @PostMapping("/{idHoaDon}/chi-tiet")
    public ApiResponse<HoaDonResponse> themMon(
            @PathVariable Integer idHoaDon,
            @RequestBody ThemMonRequest request) {
        return new ApiResponse<>(
                200,
                "Thêm món vào hóa đơn thành công",
                service.themMon(idHoaDon, request)
        );
    }
    @PatchMapping("/chi-tiet/{idChiTiet}/so-luong")
    public ApiResponse<HoaDonResponse> capNhatSoLuong(
            @PathVariable Integer idChiTiet,
            @RequestBody CapNhatSoLuongRequest request) {
        return new ApiResponse<>(
                200,
                "Cập nhật số lượng thành công",
                service.capNhatSoLuong(idChiTiet, request)
        );
    }
    @DeleteMapping("/chi-tiet/{idChiTiet}")
    public ApiResponse<HoaDonResponse> xoaMon(
            @PathVariable Integer idChiTiet) {
        return new ApiResponse<>(
                200,
                "Xóa món khỏi hóa đơn thành công",
                service.xoaMon(idChiTiet)
        );
    }
    @PostMapping("/{id}/thanh-toan")
    public ApiResponse<HoaDonResponse> thanhToanHoaDon(
            @PathVariable Integer id,
            @RequestBody ThanhToanHoaDonRequest request) {
        return new ApiResponse<>(
                200,
                "Thanh toán hóa đơn thành công",
                service.thanhToanHoaDon(id, request)
        );
    }
    @PostMapping("/{id}/payos")
    public ApiResponse<PayOSCreateResponse> taoThanhToanPayOS(
            @PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Tạo mã thanh toán PayOS thành công",
                service.taoThanhToanPayOS(id)
        );
    }
    @GetMapping("/{id}/payos/status")
    public ApiResponse<PayOSPaymentStatusResponse> layTrangThaiPayOS(
            @PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Lấy trạng thái thanh toán PayOS thành công",
                service.layTrangThaiPayOS(id)
        );
    }
    @PostMapping("/{id}/payos/cancel")
    public ApiResponse<PayOSPaymentStatusResponse> huyThanhToanPayOS(
            @PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Hủy thanh toán PayOS thành công",
                service.huyThanhToanPayOS(id)
        );
    }
    @PostMapping("/chi-tiet/{idChiTiet}/topping")
    public ApiResponse<HoaDonResponse> themTopping(
            @PathVariable Integer idChiTiet,
            @Valid @RequestBody ThemToppingRequest request) {
        return new ApiResponse<>(
                201,
                "Thêm topping vào hóa đơn thành công",
                service.themTopping(idChiTiet, request)
        );
    }
    @PutMapping("/topping/{idHdctTopping}")
    public ApiResponse<HoaDonResponse> capNhatTopping(
            @PathVariable Integer idHdctTopping,
            @Valid @RequestBody CapNhatToppingRequest request) {
        return new ApiResponse<>(
                200,
                "Cập nhật topping thành công",
                service.capNhatTopping(idHdctTopping, request)
        );
    }
    @DeleteMapping("/topping/{idHdctTopping}")
    public ApiResponse<HoaDonResponse> xoaTopping(
            @PathVariable Integer idHdctTopping) {
        return new ApiResponse<>(
                200,
                "Xóa topping khỏi hóa đơn thành công",
                service.xoaTopping(idHdctTopping)
        );
    }
    @PatchMapping("/{id}/huy")
    public ApiResponse<HoaDonResponse> huyHoaDon(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Hủy hóa đơn thành công",
                service.huyHoaDon(id)
        );
    }
    @PatchMapping("/{id}/khach-hang")
    public ApiResponse<HoaDonResponse> capNhatKhachHang(
            @PathVariable Integer id,
            @RequestBody CapNhatKhachHangHoaDonRequest request) {
        return new ApiResponse<>(
                200,
                "Cập nhật khách hàng cho hóa đơn thành công",
                service.capNhatKhachHang(id, request)
        );
    }
    @PatchMapping("/{id}/voucher")
    public ApiResponse<HoaDonResponse> apDungVoucher(
            @PathVariable Integer id,
            @RequestBody ApDungVoucherRequest request) {
        return new ApiResponse<>(
                200,
                "Áp dụng voucher thành công",
                service.apDungVoucher(id, request)
        );
    }
    @DeleteMapping("/{id}/voucher")
    public ApiResponse<HoaDonResponse> boVoucher(
            @PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Bỏ voucher thành công",
                service.boVoucher(id)
        );
    }
    @GetMapping("/{id}/voucher-kha-dung")
    public ApiResponse<Page<VoucherKhaDungResponse>> getVoucherKhaDung(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return new ApiResponse<>(
                200,
                "Lấy danh sách voucher khả dụng thành công",
                service.getVoucherKhaDung(id, page, size)
        );
    }
}