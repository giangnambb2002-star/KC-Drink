package com.example.datn.dashboard.service;

import com.example.datn.dashboard.dto.DashboardResponse;
import com.example.datn.khach_hang.repository.KhachHangRepository;
import com.example.datn.nguyen_lieu.repository.LoNguyenLieuRepository;
import com.example.datn.nguyen_lieu.repository.NguyenLieuRepository;
import com.example.datn.nhan_vien.repository.NhanVienRepository;
import com.example.datn.topping.repository.LoToppingRepository;
import com.example.datn.topping.repository.ToppingRepository;
import com.example.datn.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final VoucherRepository voucherRepository;
    private final ToppingRepository toppingRepository;
    private final NguyenLieuRepository nguyenLieuRepository;
    private final LoToppingRepository loToppingRepository;
    private final LoNguyenLieuRepository loNguyenLieuRepository;

    public DashboardResponse getDashboard() {
        LocalDate homNay = LocalDate.now();
        LocalDate bayNgayToi = homNay.plusDays(7);

        return new DashboardResponse(
                khachHangRepository.countByTrangThai(1),
                nhanVienRepository.countByTrangThai(1),
                voucherRepository.countVoucherDangHoatDong(),
                toppingRepository.countByTrangThai(1),
                nguyenLieuRepository.countByTrangThai(1),
                loToppingRepository.countSapHetHan(homNay, bayNgayToi),
                loNguyenLieuRepository.countSapHetHan(homNay, bayNgayToi),
                nguyenLieuRepository.countNguyenLieuDuoiNguong()
        );
    }
}