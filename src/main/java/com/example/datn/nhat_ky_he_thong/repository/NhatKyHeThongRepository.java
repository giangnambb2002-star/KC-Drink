package com.example.datn.nhat_ky_he_thong.repository;

import com.example.datn.nhat_ky_he_thong.entity.NhatKyHeThong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface NhatKyHeThongRepository extends JpaRepository<NhatKyHeThong, Integer> {

    @Query("SELECT n FROM NhatKyHeThong n WHERE " +
            "(:username IS NULL OR LOWER(n.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
            "(:hanhDong IS NULL OR n.hanhDong = :hanhDong) AND " +
            "(:doiTuong IS NULL OR n.doiTuong = :doiTuong) AND " +
            "(:tuNgay IS NULL OR n.thoiGian >= :tuNgay) AND " +
            "(:denNgay IS NULL OR n.thoiGian <= :denNgay)")
    Page<NhatKyHeThong> search(
            @Param("username") String username,
            @Param("hanhDong") String hanhDong,
            @Param("doiTuong") String doiTuong,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            Pageable pageable
    );
}