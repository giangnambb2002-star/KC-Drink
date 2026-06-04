package com.example.datn.khach_hang.controller;

import com.example.datn.khach_hang.model.KhachHang;
import com.example.datn.khach_hang.repository.KhachHangRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KhachHangController {

    private final KhachHangRepository khachHangRepository;

    @GetMapping("/khach-hang/tim")
    public KhachHang timKhachHang(@RequestParam String sdt) {

        return khachHangRepository
                .findBySdt(sdt)
                .orElse(null);
    }
}