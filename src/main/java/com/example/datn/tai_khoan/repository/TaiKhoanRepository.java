package com.example.datn.tai_khoan.repository;

import com.example.datn.tai_khoan.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {

    Optional<TaiKhoan> findByUsername(String username);

}