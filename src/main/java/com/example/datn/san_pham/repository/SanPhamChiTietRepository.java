package com.example.datn.san_pham.repository;

import com.example.datn.san_pham.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SanPhamChiTietRepository
        extends JpaRepository<SanPhamChiTiet, Integer> {
    SanPhamChiTiet findByIdSanPham(Integer idSanPham);
}
