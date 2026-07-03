package com.example.datn.voucher.controller;

import com.example.datn.common.ApiResponse;
import com.example.datn.common.PageResponse;
import com.example.datn.voucher.dto.VoucherRequest;
import com.example.datn.voucher.dto.VoucherResponse;
import com.example.datn.voucher.dto.VoucherValidationResponse;
import com.example.datn.voucher.service.VoucherMailService;
import com.example.datn.voucher.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/voucher")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VoucherController {

    private final VoucherService service;
    private final VoucherMailService mailService;

    // ==========================================
    // 1. API LẤY DANH SÁCH & TÌM KIẾM
    // ==========================================
    @GetMapping
    public ApiResponse<PageResponse<VoucherResponse>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idVoucher") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return new ApiResponse<>(
                200,
                "Lấy danh sách voucher thành công",
                service.getAll(keyword, trangThai, page, size, sortBy, direction)
        );
    }

    // ==========================================
    // 2. API QUẢN LÝ CHO ADMIN (THÊM, SỬA, KHÓA, MỞ KHÓA)
    // ==========================================

    @PostMapping
    public ApiResponse<VoucherResponse> create(@RequestBody VoucherRequest request) {
        return new ApiResponse<>(
                200,
                "Thêm mới mã giảm giá thành công",
                service.create(request)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<VoucherResponse> update(@PathVariable Integer id, @RequestBody VoucherRequest request) {
        return new ApiResponse<>(
                200,
                "Cập nhật mã giảm giá thành công",
                service.update(id, request)
        );
    }

    @PutMapping("/{id}/lock")
    public ApiResponse<VoucherResponse> lock(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Khóa mã giảm giá thành công",
                service.lock(id)
        );
    }

    @PutMapping("/{id}/unlock")
    public ApiResponse<VoucherResponse> unlock(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Mở khóa mã giảm giá thành công",
                service.unlock(id)
        );
    }

    // ==========================================
    // 3. API DÙNG CHO NGHIỆP VỤ KHÁCH HÀNG & HỆ THỐNG
    // ==========================================

    // Dùng cho Frontend gọi lúc khách bấm nút Áp dụng mã khi thanh toán
    @PostMapping("/validate")
    public ApiResponse<VoucherValidationResponse> validateVoucher(@RequestBody VoucherRequest request) {
        return new ApiResponse<>(
                200,
                "Kiểm tra mã giảm giá thành công",
                service.kiemTraVoucher(request)
        );
    }

    // Nút Demo gửi mail sinh nhật
    @PostMapping("/trigger-birthday")
    public ApiResponse<String> triggerBirthdayMail() {
        mailService.tangVoucherSinhNhat();
        return new ApiResponse<>(
                200,
                "Đã quét và gửi mail voucher sinh nhật thành công!",
                null
        );
    }
}