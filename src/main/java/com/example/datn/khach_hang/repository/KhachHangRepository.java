package com.example.datn.khach_hang.repository;

import com.example.datn.khach_hang.entity.KhachHang;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KhachHangRepository
        extends JpaRepository<KhachHang, Integer> {

    Optional<KhachHang> findByTaiKhoan(
            TaiKhoan taiKhoan
    );
}