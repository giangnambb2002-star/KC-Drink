package com.example.datn.nguyen_lieu.repository;

import com.example.datn.nguyen_lieu.entity.CongThucSanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CongThucSanPhamRepository extends JpaRepository<CongThucSanPham, Integer> {
    Page<CongThucSanPham> findByIdSanPhamAndIdSize(Integer idSanPham, Integer idSize, Pageable pageable);
}