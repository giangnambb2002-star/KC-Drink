package com.example.datn.ban_thanh_pham.repository;

import com.example.datn.ban_thanh_pham.entity.CongThucSanPhamBtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CongThucSanPhamBtpRepository
        extends JpaRepository<CongThucSanPhamBtp, Integer> {

    List<CongThucSanPhamBtp> findByIdSanPhamAndIdSize(
            Integer idSanPham,
            Integer idSize
    );

    boolean existsByIdSanPhamAndIdSizeAndBanThanhPham_IdBanThanhPham(
            Integer idSanPham,
            Integer idSize,
            Integer idBanThanhPham
    );
}