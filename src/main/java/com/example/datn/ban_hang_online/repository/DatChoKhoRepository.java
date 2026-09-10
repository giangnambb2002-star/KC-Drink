package com.example.datn.ban_hang_online.repository;

import com.example.datn.ban_hang_online.entity.DatChoKho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DatChoKhoRepository
        extends JpaRepository<DatChoKho, Integer> {

    List<DatChoKho> findAllByHoaDon_IdHoaDon(
            Integer idHoaDon
    );

    List<DatChoKho> findAllByHoaDon_IdHoaDonAndTrangThai(
            Integer idHoaDon,
            String trangThai
    );

    Optional<DatChoKho>
    findByHoaDon_IdHoaDonAndLoaiKhoAndIdDoiTuong(
            Integer idHoaDon,
            String loaiKho,
            Integer idDoiTuong
    );

    @Query("""
            select coalesce(sum(d.soLuong), 0)
            from DatChoKho d
            where d.loaiKho = :loaiKho
              and d.idDoiTuong = :idDoiTuong
              and d.trangThai = 'ACTIVE'
              and d.hetHanLuc > :thoiDiem
            """)
    BigDecimal tongSoLuongDangGiu(
            @Param("loaiKho") String loaiKho,
            @Param("idDoiTuong") Integer idDoiTuong,
            @Param("thoiDiem") LocalDateTime thoiDiem
    );
}