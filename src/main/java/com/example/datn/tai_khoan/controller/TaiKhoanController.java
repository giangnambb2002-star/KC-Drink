package com.example.datn.tai_khoan.controller;

import com.example.datn.tai_khoan.dto.TaiKhoanRequest;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import com.example.datn.tai_khoan.service.TaiKhoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tai-khoan")
@RequiredArgsConstructor
public class TaiKhoanController {

    private final TaiKhoanService service;

    @GetMapping
    public List<TaiKhoan> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TaiKhoan getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public TaiKhoan create(
            @Valid @RequestBody TaiKhoanRequest request
    ){
        return service.create(request);
    }

    @PutMapping("/{id}")
    public TaiKhoan update(
            @PathVariable Integer id,
            @Valid @RequestBody TaiKhoanRequest request
    ) {

        return service.update(id, request);
    }
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "Xóa thành công";
    }

    @PatchMapping("/{id}/lock")
    public TaiKhoan lock(@PathVariable Integer id) {
        return service.lock(id);
    }

    @PatchMapping("/{id}/unlock")
    public TaiKhoan unlock(@PathVariable Integer id) {
        return service.unlock(id);
    }


}