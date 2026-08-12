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

    long countByTrangThai(Integer trangThai);


//    canh bao ton kho
    @Query(value = """
        SELECT COUNT(*)
        FROM NGUYEN_LIEU n
        WHERE n.trang_thai = 1
        AND n.nguong_ton_kho IS NOT NULL
        AND (
            SELECT COALESCE(SUM(l.so_luong_ton), 0)
            FROM LO_NGUYEN_LIEU l
            WHERE l.id_nguyen_lieu = n.id_nguyen_lieu
            AND l.trang_thai = 1
            AND l.so_luong_ton > 0
            AND (l.han_su_dung IS NULL OR l.han_su_dung >= CAST(GETDATE() AS DATE))
        ) <= n.nguong_ton_kho
        """, nativeQuery = true)
    long countNguyenLieuDuoiNguong();
}