package com.example.datn.san_pham.repository;

import com.example.datn.san_pham.entity.SanPhamSize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SanPhamSizeRepository extends JpaRepository<SanPhamSize, Integer> {

    List<SanPhamSize> findByIdSanPham(Integer idSanPham);
    boolean existsByIdSanPhamAndSize_IdSize(Integer idSanPham, Integer idSize);
    Optional<SanPhamSize> findByIdSanPhamAndSize_IdSize(
            Integer idSanPham,
            Integer idSize
    );
}