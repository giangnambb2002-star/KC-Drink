package com.example.datn.khach_hang.repository;

import com.example.datn.khach_hang.entity.KhachHang;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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

}