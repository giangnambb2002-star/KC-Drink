package com.example.datn.ban_hang_online.service;
import com.example.datn.khach_hang.entity.KhachHang;
import com.example.datn.khach_hang.repository.KhachHangRepository;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class KhachHangOnlineService {
    private final KhachHangRepository khachHangRepository;
    public KhachHang getCurrentCustomer(TaiKhoan taiKhoan) {
        if (taiKhoan == null) {
            throw new RuntimeException("Chưa đăng nhập");
        }
        if (!"USER".equals(taiKhoan.getRole())) {
            throw new RuntimeException(
                    "Chức năng này chỉ dành cho khách hàng"
            );
        }
        if (taiKhoan.getTrangThai() == null
                || taiKhoan.getTrangThai() != 1) {
            throw new RuntimeException(
                    "Tài khoản hiện không hoạt động"
            );
        }
        KhachHang khachHang =
                khachHangRepository
                        .findByTaiKhoan(taiKhoan)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy hồ sơ khách hàng"
                                )
                        );
        if (khachHang.getTrangThai() == null
                || khachHang.getTrangThai() != 1) {
            throw new RuntimeException(
                    "Khách hàng hiện không hoạt động"
            );
        }
        return khachHang;
    }
}