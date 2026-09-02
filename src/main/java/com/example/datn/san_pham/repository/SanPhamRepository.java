package com.example.datn.san_pham.repository;

import com.example.datn.san_pham.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {
    Page<SanPham> findByTrangThai(Integer trangThai, Pageable pageable);
    @Query("SELECT s FROM SanPham s WHERE " +
            "(:trangThai IS NULL OR s.trangThai = :trangThai) AND " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(s.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<SanPham> searchSanPham(
            @Param("keyword") String keyword,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );

    boolean existsByTenSanPhamIgnoreCase(String tenSanPham);

    boolean existsByTenSanPhamIgnoreCaseAndIdSanPhamNot(
            String tenSanPham,
            Integer idSanPham
    );
}