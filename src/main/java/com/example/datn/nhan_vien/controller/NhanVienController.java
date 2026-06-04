package com.example.datn.nhan_vien.controller;

import com.example.datn.danh_muc.repository.DanhMucRepository;
import com.example.datn.hoa_don.repository.HoaDonRepository;
import com.example.datn.san_pham.model.SanPham;
import com.example.datn.san_pham.repository.SanPhamRepository;
import com.example.datn.size.repository.SizeRepository;
import com.example.datn.topping.repository.ToppingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NhanVienController {

    private final DanhMucRepository danhMucRepository;
    private final SanPhamRepository sanPhamRepository;
    private final SizeRepository sizeRepository;
    private final ToppingRepository toppingRepository;

    @GetMapping("/nhan-vien")
    public String home() {
        return "redirect:/nhan-vien/ban-hang";
    }

    @GetMapping("/nhan-vien/ban-hang")
    public String banHang(
            @RequestParam(value = "danhMucId", required = false) Integer danhMucId,
            Model model) {

        model.addAttribute("menuActive", "ban-hang");

        model.addAttribute("danhMucs", danhMucRepository.findAll());
        model.addAttribute("danhMucIdHienTai", danhMucId);

        List<SanPham> danhSachSanPham;

        if (danhMucId != null) {
            danhSachSanPham = sanPhamRepository.findByIdDanhMuc(danhMucId);
        } else {
            danhSachSanPham = sanPhamRepository.findAll();
        }

        model.addAttribute("sanPhams", danhSachSanPham);
        model.addAttribute("sizes", sizeRepository.findAll());
        model.addAttribute("toppings", toppingRepository.findAll());

        return "nhan-vien/ban-hang";
    }
}
