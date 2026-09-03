package com.example.datn.voucher.repository;

import com.example.datn.voucher.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {

    Optional<Voucher> findByMaVoucher(String maVoucher);

    @Query("SELECT v FROM Voucher v WHERE " +
            "v.trangThai = 1 AND " +
            "(v.soLuong IS NULL OR v.soLuong > 0) AND " +
            "(v.ngayBatDau IS NULL OR v.ngayBatDau <= :now) AND " +
            "(v.ngayKetThuc IS NULL OR v.ngayKetThuc >= :now) AND " +
            "(v.dieuKien IS NULL OR v.dieuKien <= :tongTien) AND " +
            "(v.idKhachHang IS NULL OR v.idKhachHang = :idKhachHang) AND " +
            "v.loaiVoucher IS NOT NULL AND v.giaTriGiam IS NOT NULL")
    List<Voucher> findVoucherKhaDung(
            @Param("tongTien") BigDecimal tongTien,
            @Param("idKhachHang") Integer idKhachHang,
            @Param("now") LocalDateTime now
    );

    @Query("SELECT v FROM Voucher v WHERE " +
            "(:trangThai IS NULL OR v.trangThai = :trangThai) AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(v.maVoucher) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(v.tenVoucher) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Voucher> searchVoucher(
            @Param("keyword") String keyword,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );

    @Query("SELECT COUNT(v) FROM Voucher v WHERE v.trangThai = 1 " +
            "AND (v.soLuong IS NULL OR v.soLuong > 0) " +
            "AND v.ngayBatDau <= CURRENT_TIMESTAMP " +
            "AND v.ngayKetThuc >= CURRENT_TIMESTAMP")
    long countVoucherDangHoatDong();
}