package com.example.datn.khach_hang.controller;

import com.example.datn.khach_hang.dto.KhachHangRequest;
import com.example.datn.khach_hang.entity.KhachHang;
import com.example.datn.khach_hang.service.KhachHangService;
import com.example.datn.khach_hang.dto.KhachHangResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/khach-hang")
@RequiredArgsConstructor
public class KhachHangController {

    private final KhachHangService service;

    @GetMapping
    public List<KhachHangResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public KhachHangResponse getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public KhachHangResponse create(
            @Valid @RequestBody KhachHangRequest request
    ) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public KhachHangResponse update(
            @PathVariable Integer id,
            @Valid @RequestBody KhachHangRequest request
    ) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/lock")
    public KhachHangResponse lock(@PathVariable Integer id) {
        return service.lock(id);
    }

    @PatchMapping("/{id}/unlock")
    public KhachHangResponse unlock(@PathVariable Integer id) {
        return service.unlock(id);
    }
}