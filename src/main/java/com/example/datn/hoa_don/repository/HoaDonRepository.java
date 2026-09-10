package com.example.datn.hoa_don.repository;

import com.example.datn.hoa_don.entity.HoaDon;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {

    Optional<HoaDon> findByMaHoaDon(String maHoaDon);

    Optional<HoaDon> findByClientRequestId(String clientRequestId);

    Page<HoaDon> findByKhachHang_IdKhachHangAndLoaiHoaDon(
            Integer idKhachHang,
            String loaiHoaDon,
            Pageable pageable
    );
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT h FROM HoaDon h WHERE h.idHoaDon = :idHoaDon")
    Optional<HoaDon> findByIdForUpdate(
            @Param("idHoaDon") Integer idHoaDon
    );
    @EntityGraph(attributePaths = {
            "khachHang",
            "nhanVien"
    })
    @Query(
            value = """
                SELECT DISTINCT h
                FROM HoaDon h
                LEFT JOIN h.khachHang kh
                LEFT JOIN VanDonGhn v
                    ON v.hoaDon.idHoaDon = h.idHoaDon
                WHERE (
                    :keyword IS NULL
                    OR :keyword = ''
                    OR LOWER(h.maHoaDon)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(COALESCE(kh.tenKhachHang, ''))
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR COALESCE(kh.sdt, '')
                        LIKE CONCAT('%', :keyword, '%')
                    OR LOWER(COALESCE(v.maVanDonGhn, ''))
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
                AND (
                    :trangThai IS NULL
                    OR :trangThai = ''
                    OR h.trangThai = :trangThai
                )
                AND (
                    :loaiHoaDon IS NULL
                    OR :loaiHoaDon = ''
                    OR h.loaiHoaDon = :loaiHoaDon
                )
                    AND (
                    :hinhThucThanhToan IS NULL
                    OR :hinhThucThanhToan = ''
                    OR h.hinhThucThanhToan = :hinhThucThanhToan
                    )
                AND (
                    :coGiaoHang IS NULL
                    OR (:coGiaoHang = true AND v.idVanDon IS NOT NULL)
                    OR (:coGiaoHang = false AND v.idVanDon IS NULL)
                )
                AND (
                    :trangThaiGhn IS NULL
                    OR :trangThaiGhn = ''
                    OR v.trangThaiGhn = :trangThaiGhn
                )
                    AND (
                    :trangThaiVanDon IS NULL
                    OR :trangThaiVanDon = ''
                
                    OR (
                        :trangThaiVanDon = 'CHO_XU_LY'
                        AND v.idVanDon IS NOT NULL
                        AND (
                            v.maVanDonGhn IS NULL
                            OR v.maVanDonGhn = ''
                        )
                        AND v.trangThai IN (
                            'CHO_TAO_DON',
                            'DA_TIEP_NHAN'
                        )
                        AND h.trangThai <> 'DA_HUY'
                    )
                
                    OR (
                        :trangThaiVanDon = 'DA_TAO_DON'
                        AND v.idVanDon IS NOT NULL
                        AND v.maVanDonGhn IS NOT NULL
                        AND v.maVanDonGhn <> ''
                        AND h.trangThai <> 'DA_HUY'
                        AND v.trangThai <> 'DA_HUY'
                    )
                    )
                AND (
                    :tuNgay IS NULL
                    OR h.ngayTao >= :tuNgay
                )
                AND (
                    :denNgayExclusive IS NULL
                    OR h.ngayTao < :denNgayExclusive
                )
                
                """,
            countQuery = """
                SELECT COUNT(DISTINCT h.idHoaDon)
                FROM HoaDon h
                LEFT JOIN h.khachHang kh
                LEFT JOIN VanDonGhn v
                    ON v.hoaDon.idHoaDon = h.idHoaDon
                WHERE (
                    :keyword IS NULL
                    OR :keyword = ''
                    OR LOWER(h.maHoaDon)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(COALESCE(kh.tenKhachHang, ''))
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR COALESCE(kh.sdt, '')
                        LIKE CONCAT('%', :keyword, '%')
                    OR LOWER(COALESCE(v.maVanDonGhn, ''))
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
                AND (
                    :trangThai IS NULL
                    OR :trangThai = ''
                    OR h.trangThai = :trangThai
                )
                AND (
                    :loaiHoaDon IS NULL
                    OR :loaiHoaDon = ''
                    OR h.loaiHoaDon = :loaiHoaDon
                )
                    AND (
                    :hinhThucThanhToan IS NULL
                    OR :hinhThucThanhToan = ''
                    OR h.hinhThucThanhToan = :hinhThucThanhToan
                    )
                   
                AND (
                    :coGiaoHang IS NULL
                    OR (:coGiaoHang = true AND v.idVanDon IS NOT NULL)
                    OR (:coGiaoHang = false AND v.idVanDon IS NULL)
                )
                AND (
                    :trangThaiGhn IS NULL
                    OR :trangThaiGhn = ''
                    OR v.trangThaiGhn = :trangThaiGhn
                )
                    AND (
                    :trangThaiVanDon IS NULL
                    OR :trangThaiVanDon = ''
                
                    OR (
                        :trangThaiVanDon = 'CHO_XU_LY'
                        AND v.idVanDon IS NOT NULL
                        AND (
                            v.maVanDonGhn IS NULL
                            OR v.maVanDonGhn = ''
                        )
                        AND v.trangThai IN (
                            'CHO_TAO_DON',
                            'DA_TIEP_NHAN'
                        )
                        AND h.trangThai <> 'DA_HUY'
                    )
                
                    OR (
                        :trangThaiVanDon = 'DA_TAO_DON'
                        AND v.idVanDon IS NOT NULL
                        AND v.maVanDonGhn IS NOT NULL
                        AND v.maVanDonGhn <> ''
                        AND h.trangThai <> 'DA_HUY'
                        AND v.trangThai <> 'DA_HUY'
                    )
                    )
                AND (
                    :tuNgay IS NULL
                    OR h.ngayTao >= :tuNgay
                )
                AND (
                    :denNgayExclusive IS NULL
                    OR h.ngayTao < :denNgayExclusive
                )
                    AND (
                    :hinhThucThanhToan IS NULL
                    OR :hinhThucThanhToan = ''
                    OR h.hinhThucThanhToan = :hinhThucThanhToan
                    )
                """
    )

    Page<HoaDon> searchHoaDon(
            @Param("keyword") String keyword,
            @Param("trangThai") String trangThai,
            @Param("loaiHoaDon") String loaiHoaDon,
            @Param("hinhThucThanhToan") String hinhThucThanhToan,
            @Param("coGiaoHang") Boolean coGiaoHang,
            @Param("trangThaiGhn") String trangThaiGhn,
            @Param("trangThaiVanDon") String trangThaiVanDon,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgayExclusive") LocalDateTime denNgayExclusive,
            Pageable pageable
    );
    @Query("""
    SELECT h.idHoaDon
    FROM HoaDon h
    WHERE h.loaiHoaDon = 'ONLINE'
      AND h.trangThai = 'CHO_THANH_TOAN'
      AND h.payosExpiresAt IS NOT NULL
      AND h.payosExpiresAt <= :now
""")
    List<Integer> findIdDonOnlineQuaHan(
            @Param("now") LocalDateTime now
    );
    long countByLoaiHoaDonAndNgayTaoGreaterThanEqualAndNgayTaoLessThan(
            String loaiHoaDon,
            LocalDateTime tuNgay,
            LocalDateTime denNgay
    );

    long countByTrangThaiAndNgayTaoGreaterThanEqualAndNgayTaoLessThan(
            String trangThai,
            LocalDateTime tuNgay,
            LocalDateTime denNgay
    );

    @Query("""
    SELECT COALESCE(SUM(h.thanhTien), 0)
    FROM HoaDon h
    WHERE h.trangThai = 'DA_THANH_TOAN'
      AND h.ngayTao >= :tuNgay
      AND h.ngayTao < :denNgay
""")
    java.math.BigDecimal sumDoanhThuDaThanhToan(
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay
    );
    @Query("""
    SELECT new com.example.datn.dashboard.dto.DashboardNhanVienResponse(
        nv.idNhanVien,
        nv.tenNhanVien,
        COUNT(h.idHoaDon),
        COALESCE(SUM(h.thanhTien), 0)
    )
    FROM HoaDon h
    JOIN h.nhanVien nv
    WHERE h.trangThai = 'DA_THANH_TOAN'
      AND h.ngayTao >= :tuNgay
      AND h.ngayTao < :denNgay
    GROUP BY nv.idNhanVien, nv.tenNhanVien
    ORDER BY COALESCE(SUM(h.thanhTien), 0) DESC
""")
    List<com.example.datn.dashboard.dto.DashboardNhanVienResponse>
    findThongKeNhanVien(
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay
    );
}