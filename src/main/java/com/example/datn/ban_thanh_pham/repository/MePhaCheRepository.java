package com.example.datn.ban_thanh_pham.repository;

import com.example.datn.ban_thanh_pham.entity.MePhaChe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MePhaCheRepository extends JpaRepository<MePhaChe, Integer> {

    List<MePhaChe> findByBanThanhPham_IdBanThanhPhamAndTrangThaiOrderByNgayPhaAsc(
            Integer idBanThanhPham,
            Integer trangThai
    );

    List<MePhaChe> findByBanThanhPham_IdBanThanhPhamAndTrangThai(
            Integer idBanThanhPham,
            Integer trangThai
    );
}