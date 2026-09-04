package com.example.datn.khuyen_mai.repository;

import com.example.datn.khuyen_mai.entity.KhuyenMai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KhuyenMaiRepository
        extends JpaRepository<KhuyenMai, Integer> {

    @Query("""
            SELECT km
            FROM KhuyenMai km
            WHERE (
                :keyword IS NULL
                OR :keyword = ''
                OR LOWER(km.tenKm) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND (
                :trangThai IS NULL
                OR km.trangThai = :trangThai
            )
            """)
    Page<KhuyenMai> search(
            @Param("keyword") String keyword,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );

    boolean existsByTenKmIgnoreCase(String tenKm);

    boolean existsByTenKmIgnoreCaseAndIdKmNot(
            String tenKm,
            Integer idKm
    );
}