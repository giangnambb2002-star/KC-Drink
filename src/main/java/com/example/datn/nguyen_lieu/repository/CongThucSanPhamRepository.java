package com.example.datn.nguyen_lieu.repository;

import com.example.datn.nguyen_lieu.entity.CongThucSanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CongThucSanPhamRepository extends JpaRepository<CongThucSanPham, Integer> {

    Page<CongThucSanPham> findByIdSanPhamAndIdSize(
            Integer idSanPham,
            Integer idSize,
            Pageable pageable
    );

    List<CongThucSanPham> findByIdSanPhamAndIdSize(
            Integer idSanPham,
            Integer idSize
    );
    boolean existsByIdSanPhamAndIdSizeAndNguyenLieu_IdNguyenLieu(
            Integer idSanPham,
            Integer idSize,
            Integer idNguyenLieu
    );

    boolean existsByIdSanPhamAndIdSizeAndNguyenLieu_IdNguyenLieuAndIdCtspNot(
            Integer idSanPham,
            Integer idSize,
            Integer idNguyenLieu,
            Integer idCtsp
    );
}