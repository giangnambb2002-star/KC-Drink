package com.example.datn.san_pham.controller;

import com.example.datn.danh_muc.service.DanhMucService;
import com.example.datn.san_pham.entity.SanPham;
import com.example.datn.san_pham.service.SanPhamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/nhan-vien/san-pham")
public class SanPhamController {

    private final SanPhamService service;
    private final DanhMucService danhMucService;

    @GetMapping
    public String index(Model model) {

        model.addAttribute("menuActive", "san-pham");
        model.addAttribute("list", service.getAll());
        model.addAttribute("sanPham", new SanPham());
        model.addAttribute("danhMucs", danhMucService.getAll());

        return "nhan-vien/san-pham";
    }

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
            model.addAttribute("list", service.getAll());

            return "nhan-vien/san-pham";
        }

        sanPham.setNgayTao(LocalDate.now());

        service.add(sanPham);

        return "redirect:/nhan-vien/san-pham";
    }

    @GetMapping("/detail/{id}")
    public String detail(
            @PathVariable Integer id,
            Model model) {

        model.addAttribute("menuActive", "san-pham");
        model.addAttribute("sanPham", service.getById(id));
        model.addAttribute("list", service.getAll());
        model.addAttribute("danhMucs", danhMucService.getAll());

        return "nhan-vien/san-pham";
    }

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
            model.addAttribute("list", service.getAll());

            return "nhan-vien/san-pham";
        }

        service.update(
                sanPham.getIdSanPham(),
                sanPham
        );

        return "redirect:/nhan-vien/san-pham";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {

        try {
            service.delete(id);
        } catch (Exception e) {
            System.out.println("Không thể xóa sản phẩm vì đã tồn tại trong hóa đơn");
        }

        return "redirect:/nhan-vien/san-pham";
    }
    @GetMapping("/toggle/{id}")
    public String toggle(@PathVariable Integer id) {

        SanPham sp = service.getById(id);

        if (sp != null) {

            Boolean trangThai = sp.getTrangThai();

            if (trangThai == null) {
                trangThai = true;
            }

            sp.setTrangThai(!trangThai);

            service.add(sp);
        }

        return "redirect:/nhan-vien/san-pham";
    }
}