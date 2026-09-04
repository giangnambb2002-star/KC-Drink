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

import java.util.Optional;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {

    Optional<HoaDon> findByMaHoaDon(String maHoaDon);

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
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgayExclusive") LocalDateTime denNgayExclusive,
            Pageable pageable
    );
}