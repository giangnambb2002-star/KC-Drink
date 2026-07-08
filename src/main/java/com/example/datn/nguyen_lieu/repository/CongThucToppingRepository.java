package com.example.datn.nguyen_lieu.repository;

import com.example.datn.nguyen_lieu.entity.CongThucTopping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CongThucToppingRepository extends JpaRepository<CongThucTopping, Integer> {
    // Tìm tất cả nguyên liệu cấu thành nên 1 Topping cụ thể
    Page<CongThucTopping> findByIdTopping(Integer idTopping, Pageable pageable);
}