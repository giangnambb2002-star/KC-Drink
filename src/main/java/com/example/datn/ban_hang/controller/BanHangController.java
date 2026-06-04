package com.example.datn.ban_hang.controller;

import com.example.datn.ban_hang.dto.CartItem;
import com.example.datn.ban_hang.service.BanHangService;
import com.example.datn.hoa_don.repository.HoaDonChiTietRepository;
import com.example.datn.hoa_don.repository.HoaDonRepository;
import com.example.datn.khach_hang.repository.KhachHangRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ban-hang")
@RequiredArgsConstructor
public class BanHangController {

    private final BanHangService banHangService;

    private final HoaDonRepository hoaDonRepository;

    private final HoaDonChiTietRepository hoaDonChiTietRepository;

    private final KhachHangRepository khachHangRepository;

    @PostMapping("/them-gio")
    public String themGio(@RequestBody CartItem item, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        boolean found = false;
        String newItemTopping = item.getTenTopping() == null ? "" : item.getTenTopping().trim();

        for (CartItem c : cart) {
            String currentItemTopping = c.getTenTopping() == null ? "" : c.getTenTopping().trim();

            if (c.getSanPhamId().equals(item.getSanPhamId())
                    && c.getSizeId().equals(item.getSizeId())
                    && currentItemTopping.equals(newItemTopping)) {

                c.setSoLuong(c.getSoLuong() + item.getSoLuong());
                c.setThanhTien(c.getDonGia().multiply(BigDecimal.valueOf(c.getSoLuong())));
                found = true;
                break;
            }
        }

        if (!found) {
            cart.add(item);
        }

        session.setAttribute("cart", cart);
        return "Đã thêm vào giỏ";
    }

    @PostMapping("/thanh-toan")
    public ResponseEntity<String> thanhToan(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return ResponseEntity.badRequest().body("Giỏ hàng của bạn đang trống!");
        }

        try {
            banHangService.thanhToan(cart);
            session.removeAttribute("cart");
            return ResponseEntity.ok("Thanh toán thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi hệ thống khi lưu hóa đơn: " + e.getMessage());
        }
    }

    @GetMapping("/gio-hang")
    public List<CartItem> gioHang(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            return new ArrayList<>();
        }
        return cart;
    }

    @DeleteMapping("/xoa/{index}")
    public void xoa(@PathVariable("index") int index, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null && index >= 0 && index < cart.size()) {
            cart.remove(index);
            session.setAttribute("cart", cart);
        }
    }

    @PutMapping("/tang-sl/{index}")
    public void tangSoLuong(@PathVariable("index") int index, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null && index >= 0 && index < cart.size()) {
            CartItem item = cart.get(index);
            item.setSoLuong(item.getSoLuong() + 1);
            item.setThanhTien(item.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong())));
            session.setAttribute("cart", cart);
        }
    }

    @PutMapping("/giam-sl/{index}")
    public void giamSoLuong(@PathVariable("index") int index, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null && index >= 0 && index < cart.size()) {
            CartItem item = cart.get(index);
            if (item.getSoLuong() > 1) {
                item.setSoLuong(item.getSoLuong() - 1);
                item.setThanhTien(item.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong())));
            } else {
                cart.remove(index);
            }
            session.setAttribute("cart", cart);
        }
    }
}