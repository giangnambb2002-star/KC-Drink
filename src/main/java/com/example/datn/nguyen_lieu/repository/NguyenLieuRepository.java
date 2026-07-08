package com.example.datn.nguyen_lieu.repository;

import com.example.datn.nguyen_lieu.entity.NguyenLieu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NguyenLieuRepository extends JpaRepository<NguyenLieu, Integer> {

    @Query("SELECT n FROM NguyenLieu n WHERE " +
            "(:trangThai IS NULL OR n.trangThai = :trangThai) AND " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(n.tenNguyenLieu) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<NguyenLieu> searchNguyenLieu(
            @Param("keyword") String keyword,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );

    boolean existsByTenNguyenLieu(String tenNguyenLieu);

    // Dùng cho lúc Sửa (PUT) - Kiểm tra trùng tên nhưng bỏ qua chính ID đang sửa
    boolean existsByTenNguyenLieuAndIdNguyenLieuNot(String tenNguyenLieu, Integer idNguyenLieu);
}