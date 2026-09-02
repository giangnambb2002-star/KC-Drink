package com.example.datn.ban_thanh_pham.repository;

import com.example.datn.ban_thanh_pham.entity.CongThucBanThanhPham;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CongThucBanThanhPhamRepository
        extends JpaRepository<CongThucBanThanhPham, Integer> {

    List<CongThucBanThanhPham> findByBanThanhPham_IdBanThanhPham(
            Integer idBanThanhPham
    );
}