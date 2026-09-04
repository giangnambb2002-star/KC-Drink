package com.example.datn.khuyen_mai.repository;

import com.example.datn.khuyen_mai.entity.SanPhamKhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SanPhamKhuyenMaiRepository
        extends JpaRepository<SanPhamKhuyenMai, Integer> {

    List<SanPhamKhuyenMai> findAllByKhuyenMai_IdKm(Integer idKm);
    List<SanPhamKhuyenMai>
    findAllByKhuyenMai_IdKmOrderBySanPham_TenSanPhamAsc(Integer idKm);

    boolean existsBySanPham_IdSanPhamAndKhuyenMai_IdKm(
            Integer idSanPham,
            Integer idKm
    );

    void deleteAllByKhuyenMai_IdKm(Integer idKm);

    @Query("""
            SELECT spkm
            FROM SanPhamKhuyenMai spkm
            JOIN FETCH spkm.khuyenMai km
            WHERE spkm.sanPham.idSanPham = :idSanPham
              AND km.trangThai = 1
              AND km.ngayBatDau <= :thoiDiem
              AND km.ngayKetThuc >= :thoiDiem
            """)
    List<SanPhamKhuyenMai> findKhuyenMaiDangApDung(
            @Param("idSanPham") Integer idSanPham,
            @Param("thoiDiem") LocalDateTime thoiDiem
    );
}