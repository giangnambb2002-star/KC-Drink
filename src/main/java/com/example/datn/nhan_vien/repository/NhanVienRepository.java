package com.example.datn.nhan_vien.repository;

import com.example.datn.nhan_vien.entity.NhanVien;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {

    Optional<NhanVien> findByTaiKhoan(TaiKhoan taiKhoan);
    Page<NhanVien> findByTenNhanVienContainingIgnoreCaseOrSdtContaining(
            String ten,
            String sdt,
            Pageable pageable
    );
    Optional<NhanVien> findBySdt(String sdt);
    Optional<NhanVien> findByEmail(String email);

}