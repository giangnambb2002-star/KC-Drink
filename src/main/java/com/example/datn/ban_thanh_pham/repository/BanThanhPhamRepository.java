package com.example.datn.ban_thanh_pham.repository;

import com.example.datn.ban_thanh_pham.entity.BanThanhPham;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BanThanhPhamRepository
        extends JpaRepository<BanThanhPham, Integer> {
    boolean existsByTenBanThanhPhamIgnoreCase(String tenBanThanhPham);
}