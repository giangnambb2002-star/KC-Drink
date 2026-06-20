package com.example.datn.hoa_don.repository;

import com.example.datn.hoa_don.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HoaDonChiTietRepository
        extends JpaRepository<HoaDonChiTiet, Integer> {
    List<HoaDonChiTiet> findByHoaDon_IdHoaDon(Integer idHoaDon);
}