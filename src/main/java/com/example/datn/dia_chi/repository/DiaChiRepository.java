package com.example.datn.dia_chi.repository;

import com.example.datn.dia_chi.entity.DiaChiKhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaChiRepository extends JpaRepository<DiaChiKhachHang, Integer> {

    // Dùng cho phân trang
    Page<DiaChiKhachHang> findByKhachHang_IdKhachHang(
            Integer idKhachHang,
            Pageable pageable
    );

    // Dùng trong Service để cập nhật địa chỉ mặc định
    List<DiaChiKhachHang> findAllByKhachHang_IdKhachHang(
            Integer idKhachHang
    );

    @Query("""
select d
from DiaChiKhachHang d
where d.khachHang.idKhachHang = :idKhachHang
and d.macDinh = true
and d.trangThai = 1
""")
    Optional<DiaChiKhachHang> findDefault(Integer idKhachHang);
}