package com.example.datn.nhan_vien.controller;

import com.example.datn.danh_muc.repository.DanhMucRepository;
import com.example.datn.khach_hang.repository.KhachHangRepository;
import com.example.datn.san_pham.entity.SanPham;
import com.example.datn.san_pham.repository.SanPhamRepository;
import com.example.datn.size.repository.SizeRepository;
import com.example.datn.topping.repository.ToppingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NhanVienController {

    private final DanhMucRepository danhMucRepository;
    private final SanPhamRepository sanPhamRepository;
    private final SizeRepository sizeRepository;
    private final ToppingRepository toppingRepository;
    private final KhachHangRepository khachHangRepository;

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
    @GetMapping("/nhan-vien/khach-hang")
    public String khachHang(Model model) {

        model.addAttribute("menuActive", "khach-hang");
        model.addAttribute("danhMucs", danhMucRepository.findAll());
        model.addAttribute(
                "khachHangs",
                khachHangRepository.findAll()
        );

        return "nhan-vien/khach-hang";
    }
    @GetMapping("/nhan-vien/voucher")
    public String voucher(Model model){
        model.addAttribute("menuActive","voucher");
        model.addAttribute("danhMucs", danhMucRepository.findAll());
        return "nhan-vien/voucher";
    }

    @GetMapping("/nhan-vien/khuyen-mai")
    public String khuyenMai(Model model){
        model.addAttribute("menuActive","khuyen-mai");
        model.addAttribute("danhMucs", danhMucRepository.findAll());
        return "nhan-vien/khuyen-mai";
    }

    @GetMapping("/nhan-vien/nguyen-lieu")
    public String nguyenLieu(Model model){
        model.addAttribute("menuActive","nguyen-lieu");
        model.addAttribute("danhMucs", danhMucRepository.findAll());
        return "nhan-vien/nguyen-lieu";
    }

    @GetMapping("/nhan-vien/cong-thuc")
    public String congThuc(Model model){
        model.addAttribute("menuActive","cong-thuc");
        model.addAttribute("danhMucs", danhMucRepository.findAll());
        return "nhan-vien/cong-thuc";
    }
    @ModelAttribute("danhMucs")
    public Object loadDanhMucs() {
        return danhMucRepository.findAll();
    }
}
