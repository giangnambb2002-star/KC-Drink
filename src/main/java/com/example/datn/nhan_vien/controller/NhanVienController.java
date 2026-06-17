package com.example.datn.nhan_vien.controller;

import com.example.datn.nhan_vien.dto.NhanVienRequest;
import com.example.datn.nhan_vien.entity.NhanVien;
import com.example.datn.nhan_vien.service.NhanVienService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nhan-vien")
@RequiredArgsConstructor
public class NhanVienController {

    private final NhanVienService service;

    @GetMapping
    public List<NhanVien> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public NhanVien getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public NhanVien create(@RequestBody NhanVienRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public NhanVien update(
            @PathVariable Integer id,
            @RequestBody NhanVienRequest request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "Xóa thành công";
    }
}