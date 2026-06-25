package com.example.datn.san_pham.service;

import com.example.datn.san_pham.entity.SanPhamChiTiet;
import com.example.datn.san_pham.repository.SanPhamChiTietRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SanPhamChiTietService {

    private final SanPhamChiTietRepository repository;

    public SanPhamChiTiet getByIdSanPham(Integer idSanPham) {
        return repository.findByIdSanPham(idSanPham);
    }

    public SanPhamChiTiet save(SanPhamChiTiet spct) {
        return repository.save(spct);
    }
}
