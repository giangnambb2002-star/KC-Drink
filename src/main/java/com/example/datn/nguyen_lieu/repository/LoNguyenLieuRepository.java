package com.example.datn.nguyen_lieu.repository;

import com.example.datn.nguyen_lieu.entity.LoNguyenLieu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoNguyenLieuRepository extends JpaRepository<LoNguyenLieu, Integer> {

    @Query("SELECT l FROM LoNguyenLieu l WHERE " +
            "(:trangThai IS NULL OR l.trangThai = :trangThai) AND " +
            "(:idNguyenLieu IS NULL OR l.nguyenLieu.idNguyenLieu = :idNguyenLieu)")
    Page<LoNguyenLieu> searchLoNguyenLieu(
            @Param("idNguyenLieu") Integer idNguyenLieu,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );

    // Chuẩn bị sẵn "vũ khí" cho hàm Trừ kho FEFO ngày mai: Lấy các lô còn hạn, còn hàng, xếp theo Date gần nhất
    @Query("SELECT l FROM LoNguyenLieu l WHERE l.nguyenLieu.idNguyenLieu = :idNguyenLieu " +
            "AND l.soLuongTon > 0 AND l.trangThai = 1 AND l.hanSuDung >= CURRENT_DATE " +
            "ORDER BY l.hanSuDung ASC")
    List<LoNguyenLieu> findLoToUseFEFO(@Param("idNguyenLieu") Integer idNguyenLieu);
}