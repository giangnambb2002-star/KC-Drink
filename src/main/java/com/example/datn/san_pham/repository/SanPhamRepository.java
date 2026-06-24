package com.example.datn.san_pham.repository;

import com.example.datn.san_pham.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    Page<SanPham> findByTenSanPhamContainingIgnoreCase(
            String keyword,
            Pageable pageable
    );
    List<SanPham> findByIdDanhMuc(Integer idDanhMuc);

    boolean existsByTenSanPham(String tenSanPham);

    boolean existsByTenSanPhamAndIdSanPhamNot(
            String tenSanPham,
            Integer idSanPham
    );
}