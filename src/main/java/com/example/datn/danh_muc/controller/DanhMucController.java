package com.example.datn.danh_muc.controller;

import com.example.datn.danh_muc.model.DanhMuc;
import com.example.datn.danh_muc.service.DanhMucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/danh-muc")
public class DanhMucController {

    @Autowired
    private DanhMucService service;

    @GetMapping
    public String hienThi(Model model) {

        model.addAttribute("list", service.getAll());
        model.addAttribute("danhMuc", new DanhMuc());

        return "danh-muc/index";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute DanhMuc danhMuc) {

        service.add(danhMuc);

        return "redirect:/danh-muc";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id,
                         Model model) {

        model.addAttribute("danhMuc", service.getById(id));
        model.addAttribute("list", service.getAll());

        return "danh-muc/index";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute DanhMuc danhMuc) {

        service.update(
                danhMuc.getIdDanhMuc(),
                danhMuc
        );

        return "redirect:/danh-muc";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {

        service.delete(id);

        return "redirect:/danh-muc";
    }
}