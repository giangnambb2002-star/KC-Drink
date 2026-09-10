package com.example.datn.hoa_don.repository;

import com.example.datn.hoa_don.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import com.example.datn.dashboard.dto.DashboardSanPhamBanChayResponse;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;


public interface HoaDonChiTietRepository
        extends JpaRepository<HoaDonChiTiet, Integer> {

    List<HoaDonChiTiet> findByHoaDon_IdHoaDon(
            Integer idHoaDon
    );

    @Query("""
            SELECT c
            FROM HoaDonChiTiet c
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
              AND (
                    (:idKm IS NULL AND c.khuyenMai IS NULL)
                    OR (
                        :idKm IS NOT NULL
                        AND c.khuyenMai.idKm = :idKm
                    )
              )
            """)
    Optional<HoaDonChiTiet> findMonTrung(
            @Param("idHoaDon") Integer idHoaDon,
            @Param("idSanPham") Integer idSanPham,
            @Param("idSize") Integer idSize,
            @Param("mucDuong") Integer mucDuong,
            @Param("mucDa") Integer mucDa,
            @Param("ghiChu") String ghiChu,
            @Param("idKm") Integer idKm,
            @Param("donGia") BigDecimal donGia
    );
    @Query("""
        SELECT new com.example.datn.dashboard.dto.DashboardSanPhamBanChayResponse(
            c.idSanPham,
            sp.tenSanPham,
            SUM(c.soLuong),
            SUM(c.thanhTien)
        )
        FROM HoaDonChiTiet c, SanPham sp
        WHERE sp.idSanPham = c.idSanPham
          AND c.hoaDon.trangThai = 'DA_THANH_TOAN'
          AND c.hoaDon.ngayTao >= :tuNgay
          AND c.hoaDon.ngayTao < :denNgay
        GROUP BY c.idSanPham, sp.tenSanPham
        ORDER BY SUM(c.soLuong) DESC, SUM(c.thanhTien) DESC
        """)
    List<DashboardSanPhamBanChayResponse> findSanPhamBanChay(
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            Pageable pageable
    );
}