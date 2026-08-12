package com.example.datn.voucher.repository;

import com.example.datn.voucher.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;


import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {

    // Tìm voucher theo mã code khách nhập vào
    Optional<Voucher> findByMaVoucher(String maVoucher);
    @Query("SELECT v FROM Voucher v WHERE " +
            "(:trangThai IS NULL OR v.trangThai = :trangThai) AND " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(v.maVoucher) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(v.tenVoucher) LIKE LOWER(CONCAT('%', :keyword, '%')))")
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