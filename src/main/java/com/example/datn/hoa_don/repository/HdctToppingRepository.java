package com.example.datn.hoa_don.repository;

import com.example.datn.hoa_don.entity.HdctTopping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HdctToppingRepository extends JpaRepository<HdctTopping, Integer> {

    List<HdctTopping> findByHoaDonChiTiet_IdHoaDonChiTiet(Integer idHoaDonChiTiet);
    Optional<HdctTopping> findByHoaDonChiTiet_IdHoaDonChiTietAndIdTopping(
            Integer idHoaDonChiTiet,
            Integer idTopping
    );
    @Query("""
    SELECT COALESCE(SUM(t.soLuong), 0)
    FROM HdctTopping t
    WHERE t.idTopping = :idTopping
      AND t.hoaDonChiTiet.hoaDon.trangThai = 'CHO_THANH_TOAN'
""")
    Long getTongSoLuongDangChoThanhToan(@Param("idTopping") Integer idTopping);
}