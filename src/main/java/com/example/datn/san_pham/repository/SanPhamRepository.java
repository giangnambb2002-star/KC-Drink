package com.example.datn.san_pham.repository;

import com.example.datn.san_pham.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    List<SanPham> findByIdDanhMuc(Integer idDanhMuc);

    boolean existsByTenSanPham(String tenSanPham);

    boolean existsByTenSanPhamAndIdSanPhamNot(
            String tenSanPham,
            Integer idSanPham
    );
}