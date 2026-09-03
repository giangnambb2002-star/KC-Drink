package com.example.datn.topping.repository;

import com.example.datn.topping.entity.Topping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ToppingRepository extends JpaRepository<Topping, Integer> {

    @Query("SELECT t FROM Topping t WHERE " +
            "(:trangThai IS NULL OR t.trangThai = :trangThai) AND " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(t.tenTopping) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Topping> searchTopping(
            @Param("keyword") String keyword,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );

    // Check trùng tên để validate lúc thêm mới / cập nhật
    boolean existsByTenToppingIgnoreCase(String tenTopping);

    long countByTrangThai(Integer trangThai);
}