package com.example.datn.nhan_vien.controller;

import com.example.datn.common.ApiResponse;
import com.example.datn.common.PageResponse;
import com.example.datn.nhan_vien.dto.NhanVienRequest;
import com.example.datn.nhan_vien.dto.NhanVienResponse;
import com.example.datn.nhan_vien.dto.NhanVienUpdateRequest;
import com.example.datn.nhan_vien.service.NhanVienService;
import com.example.datn.tai_khoan.entity.TaiKhoan; // Đã thêm import này
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication; // Đã thêm import này
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nhan-vien")
@RequiredArgsConstructor
public class NhanVienController {

    private final NhanVienService service;

    // Lấy danh sách nhân viên
    @GetMapping
    public ApiResponse<PageResponse<NhanVienResponse>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String chucVu,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idNhanVien") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return new ApiResponse<>(
                200,
                "Lấy danh sách nhân viên thành công",
                service.getAll(
                        keyword,
                        chucVu,
                        trangThai,
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    public ApiResponse<NhanVienResponse> getById(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Lấy thông tin nhân viên thành công",
                service.getById(id)
        );
    }

    // Thêm mới
    @PostMapping
    public ApiResponse<NhanVienResponse> create(
            @Valid @RequestBody NhanVienRequest request) {
        return new ApiResponse<>(
                201,
                "Thêm nhân viên thành công",
                service.create(request)
        );
    }

    // Cập nhật (Đã sửa: Thêm Authentication để kiểm tra tài khoản đang đăng nhập)
    @PutMapping("/{id}")
    public ApiResponse<NhanVienResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody NhanVienUpdateRequest request,
            Authentication authentication) {

        // Lấy đối tượng tài khoản đang đăng nhập từ hệ thống bảo mật của nhóm
        TaiKhoan taiKhoanDangDangNhap = (TaiKhoan) authentication.getPrincipal();

        return new ApiResponse<>(
                200,
                "Cập nhật nhân viên thành công",
                service.update(id, request, taiKhoanDangDangNhap)
        );
    }

    @PatchMapping("/{id}/lock")
    public ApiResponse<NhanVienResponse> lock(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Khóa nhân viên thành công",
                service.lock(id)
        );
    }

    @PatchMapping("/{id}/unlock")
    public ApiResponse<NhanVienResponse> unlock(@PathVariable Integer id) {
        return new ApiResponse<>(
                200,
                "Mở khóa nhân viên thành công",
                service.unlock(id)
        );
    }
}