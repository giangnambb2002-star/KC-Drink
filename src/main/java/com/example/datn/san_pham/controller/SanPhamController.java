package com.example.datn.san_pham.controller;

import com.example.datn.danh_muc.service.DanhMucService;
import com.example.datn.san_pham.entity.SanPham;
import com.example.datn.san_pham.service.SanPhamChiTietService;
import com.example.datn.san_pham.service.SanPhamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/nhan-vien/san-pham")
public class SanPhamController {

    private final SanPhamService service;
    private final DanhMucService danhMucService;
    private final SanPhamChiTietService sanPhamChiTietService;
    // ==========================
// DANH SÁCH SẢN PHẨM
// ==========================
    @GetMapping
    public String index(
        @RequestParam(required = false) Integer danhMucId,
        @RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "0") int page,
        Model model) {
    int pageSize = 8;

    var sanPhamPage = service.search(
            keyword,
            page,
            pageSize
    );

    var list = sanPhamPage.getContent();

    if (danhMucId != null) {

        list = list.stream()
                .filter(sp ->
                        sp.getIdDanhMuc() != null
                                && sp.getIdDanhMuc().equals(danhMucId))
                .toList();
    }

    model.addAttribute("menuActive", "san-pham");
    model.addAttribute("danhMucs", danhMucService.getAll());

    model.addAttribute("list", list);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", sanPhamPage.getTotalPages());
    model.addAttribute("keyword", keyword);
    model.addAttribute("danhMucId", danhMucId);

    return "nhan-vien/san-pham";
    }
    // ==========================
// MÀN HÌNH THÊM MỚI
// ==========================
    @GetMapping("/create")
    public String create(Model model) {

        model.addAttribute("menuActive", "san-pham");
        model.addAttribute("sanPham", new SanPham());
        model.addAttribute("danhMucs", danhMucService.getAll());

        return "nhan-vien/san-pham-create";
    }

    // ==========================
// THÊM MỚI
// ==========================
    @PostMapping("/add")
    public String add(
            @Valid @ModelAttribute("sanPham") SanPham sanPham,
            BindingResult result,
            Model model) {

        if (service.isTenSanPhamExist(
                sanPham.getTenSanPham())) {

            result.rejectValue(
                    "tenSanPham",
                    "error.tenSanPham",
                    "Tên sản phẩm đã tồn tại"
            );
        }

        if (result.hasErrors()) {

            model.addAttribute("menuActive", "san-pham");
            model.addAttribute("danhMucs", danhMucService.getAll());

            return "nhan-vien/san-pham-create";
        }

        service.add(sanPham);

        return "redirect:/nhan-vien/san-pham";
    }

    // ==========================
// MÀN HÌNH SỬA
// ==========================
    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Integer id,
            Model model) {

        model.addAttribute("menuActive", "san-pham");
        model.addAttribute("sanPham", service.getById(id));
        model.addAttribute("danhMucs", danhMucService.getAll());

        return "nhan-vien/san-pham-edit";
    }

    // ==========================
// CẬP NHẬT
// ==========================
    @PostMapping("/update")
    public String update(
            @Valid @ModelAttribute("sanPham") SanPham sanPham,
            BindingResult result,
            Model model) {

        if (service.isTenSanPhamExistWhenUpdate(
                sanPham.getTenSanPham(),
                sanPham.getIdSanPham())) {

            result.rejectValue(
                    "tenSanPham",
                    "error.tenSanPham",
                    "Tên sản phẩm đã tồn tại"
            );
        }

        if (result.hasErrors()) {

            model.addAttribute("menuActive", "san-pham");
            model.addAttribute("danhMucs", danhMucService.getAll());

            return "nhan-vien/san-pham-edit";
        }

        service.update(
                sanPham.getIdSanPham(),
                sanPham
        );

        return "redirect:/nhan-vien/san-pham";
    }

    // ==========================
// CHI TIẾT
// ==========================
    @GetMapping("/detail/{id}")
    public String detail(
            @PathVariable Integer id,
            Model model) {
        SanPham sanPham = service.getById(id);

        model.addAttribute("menuActive", "san-pham");
        model.addAttribute("sanPham", sanPham);

        model.addAttribute(
                "sanPhamChiTiet",
                sanPhamChiTietService.getByIdSanPham(id)
        );

        return "nhan-vien/san-pham-detail";
    }

    @GetMapping("/toggle/{id}")
    public String toggle(@PathVariable Integer id) {

        SanPham sp = service.getById(id);

        if (sp != null) {

            if (sp.getTrangThai() == null) {
                sp.setTrangThai(true);
            }

            sp.setTrangThai(!sp.getTrangThai());

            service.add(sp);
        }

        return "redirect:/nhan-vien/san-pham";
    }
}
