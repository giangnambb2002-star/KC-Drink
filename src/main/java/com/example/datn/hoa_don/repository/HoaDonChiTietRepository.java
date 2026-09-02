package com.example.datn.hoa_don.repository;

import com.example.datn.hoa_don.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {

    List<HoaDonChiTiet> findByHoaDon_IdHoaDon(Integer idHoaDon);

    @Query("""
        SELECT c FROM HoaDonChiTiet c
        WHERE c.hoaDon.idHoaDon = :idHoaDon
          AND c.idSanPham = :idSanPham
          AND c.idSize = :idSize
          AND c.mucDuong = :mucDuong
          AND c.mucDa = :mucDa
          AND c.donGia = :donGia
          AND (
                (:ghiChu IS NULL AND c.ghiChu IS NULL)
                OR c.ghiChu = :ghiChu
              )
        """)
    Optional<HoaDonChiTiet> findMonTrung(
            @Param("idHoaDon") Integer idHoaDon,
            @Param("idSanPham") Integer idSanPham,
            @Param("idSize") Integer idSize,
            @Param("mucDuong") Integer mucDuong,
            @Param("mucDa") Integer mucDa,
            @Param("ghiChu") String ghiChu,
            @Param("donGia") BigDecimal donGia
    );
}