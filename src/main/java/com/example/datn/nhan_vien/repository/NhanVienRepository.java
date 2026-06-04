package com.example.datn.nhan_vien.repository;

import com.example.datn.nhan_vien.model.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NhanVienRepository
        extends JpaRepository<NhanVien, Integer> {
}