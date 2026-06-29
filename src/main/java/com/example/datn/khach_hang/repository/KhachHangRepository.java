package com.example.datn.khach_hang.repository;

import com.example.datn.khach_hang.entity.KhachHang;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KhachHangRepository
        extends JpaRepository<KhachHang, Integer> {

    Optional<KhachHang> findByTaiKhoan(TaiKhoan taiKhoan);
    Optional<KhachHang> findBySdt(String sdt);
    Page<KhachHang> findByTenKhachHangContainingIgnoreCaseOrSdtContaining(
            String ten,
            String sdt,
            Pageable pageable
    );
    @Query("SELECT k FROM KhachHang k WHERE " +
            "(:trangThai IS NULL OR k.trangThai = :trangThai) AND " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(k.tenKhachHang) LIKE LOWER(CONCAT('%', :keyword, '%')) OR k.sdt LIKE CONCAT('%', :keyword, '%'))")
    Page<KhachHang> searchKhachHang(
            @Param("keyword") String keyword,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );
}