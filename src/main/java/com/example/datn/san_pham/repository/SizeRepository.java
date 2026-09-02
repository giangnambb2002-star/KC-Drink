package com.example.datn.san_pham.repository;

import com.example.datn.san_pham.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SizeRepository extends JpaRepository<Size, Integer> {
    boolean existsByTenSizeIgnoreCase(String tenSize);
}