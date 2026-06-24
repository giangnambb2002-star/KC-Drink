package com.example.datn.ban_hang.service;

import com.example.datn.ban_hang.dto.CartItem;
import com.example.datn.hoa_don.entity.HoaDon;
import com.example.datn.hoa_don.entity.HoaDonChiTiet;
import com.example.datn.hoa_don.repository.HoaDonChiTietRepository;
import com.example.datn.hoa_don.repository.HoaDonRepository;
import com.example.datn.san_pham.entity.SanPham;
import com.example.datn.san_pham.repository.SanPhamRepository;
import com.example.datn.size.entity.Size;
import com.example.datn.size.repository.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.datn.ban_hang.dto.ThanhToanRequest;
import com.example.datn.khach_hang.entity.KhachHang;
import com.example.datn.khach_hang.repository.KhachHangRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BanHangService {

    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final SanPhamRepository sanPhamRepository;
    private final SizeRepository sizeRepository;
    private final KhachHangRepository khachHangRepository;

    @Transactional(rollbackFor = Exception.class)
    public void thanhToan(
            List<CartItem> cartItems,
            ThanhToanRequest request) throws Exception {

        BigDecimal tongTien = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
            tongTien = tongTien.add(item.getThanhTien());
        }

        KhachHang khachHang = null;

        if (request.getSdt() != null && !request.getSdt().isBlank()) {

            khachHang = khachHangRepository
                    .findBySdt(request.getSdt())
                    .orElse(null);

            if (khachHang == null) {

                khachHang = KhachHang.builder()
                        .tenKhachHang(request.getTenKhachHang())
                        .sdt(request.getSdt())
                        .diemTichLuy(0)
                        .build();

                khachHang = khachHangRepository.saveAndFlush(khachHang);
            }
        }

        HoaDon hdBanDau = HoaDon.builder()
                .maHoaDon("HD" + System.currentTimeMillis())
                .ngayTao(LocalDateTime.now())
                .tongTien(tongTien)
                .giamGia(BigDecimal.ZERO)
                .thanhTien(tongTien)
                .hinhThucThanhToan("TIEN_MAT")
                .trangThai("DA_THANH_TOAN")
                .ghiChu(request.getGhiChu())
                .khachHang(khachHang)
                .build();

        HoaDon hoaDonDaLuu = hoaDonRepository.saveAndFlush(hdBanDau);

        for (CartItem item : cartItems) {

            SanPham sanPham = sanPhamRepository.findById(item.getSanPhamId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            Size size = sizeRepository.findById(item.getSizeId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy size"));

            HoaDonChiTiet hdct = HoaDonChiTiet.builder()
                    .hoaDon(hoaDonDaLuu)
                    .sanPham(sanPham)
                    .size(size)
                    .tenSanPham(item.getTenSanPham())
                    .tenSize(item.getTenSize())
                    .tenTopping(item.getTenTopping())
                    .soLuong(item.getSoLuong())
                    .donGia(item.getDonGia())
                    .thanhTien(item.getThanhTien())
                    .build();

            hoaDonChiTietRepository.saveAndFlush(hdct);
        }
    }
}