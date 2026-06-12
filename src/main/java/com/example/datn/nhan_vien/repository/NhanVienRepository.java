package com.example.datn.nhan_vien.repository;

import com.example.datn.nhan_vien.entity.NhanVien;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NhanVienRepository
        extends JpaRepository<NhanVien, Integer> {

    Optional<NhanVien> findByTaiKhoan(
            TaiKhoan taiKhoan
    );
}