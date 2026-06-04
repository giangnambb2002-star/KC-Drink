package com.example.datn.ban_hang.service;

import com.example.datn.ban_hang.dto.CartItem;
import com.example.datn.hoa_don.model.HoaDon;
import com.example.datn.hoa_don.model.HoaDonChiTiet;
import com.example.datn.hoa_don.repository.HoaDonChiTietRepository;
import com.example.datn.hoa_don.repository.HoaDonRepository;
import com.example.datn.san_pham.model.SanPham;
import com.example.datn.san_pham.repository.SanPhamRepository;
import com.example.datn.size.model.Size;
import com.example.datn.size.repository.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(rollbackFor = Exception.class)
    public void thanhToan(List<CartItem> cartItems) throws Exception {
        BigDecimal tongTien = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            tongTien = tongTien.add(item.getThanhTien());
        }

        // 1. Tạo và lưu thực thể Hóa đơn gốc trước
        HoaDon hdBanDau = HoaDon.builder()
                .maHoaDon("HD" + System.currentTimeMillis())
                .ngayTao(LocalDateTime.now())
                .tongTien(tongTien)
                .giamGia(BigDecimal.ZERO)
                .thanhTien(tongTien)
                .hinhThucThanhToan("TIEN_MAT")
                .trangThai("DA_THANH_TOAN")
                .ghiChu("Bán hàng tại quầy")
                .build();

        // ÉP BUỘC ĐỒNG BỘ: saveAndFlush bắt database sinh ID tự tăng ngay lập tức
        HoaDon hoaDonDaLuu = hoaDonRepository.saveAndFlush(hdBanDau);

        // 2. Vòng lặp bóc tách từng phần tử giỏ hàng lưu vào HoaDonChiTiet
        for (CartItem item : cartItems) {
            SanPham sanPham = sanPhamRepository.findById(item.getSanPhamId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            Size size = sizeRepository.findById(item.getSizeId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy size"));

            // Đơn giá và thành tiền lấy trọn vẹn từ CartItem đã tính Topping
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

            // Lưu chi tiết hóa đơn đồng bộ xuống DB
            hoaDonChiTietRepository.saveAndFlush(hdct);
        }
    }
}