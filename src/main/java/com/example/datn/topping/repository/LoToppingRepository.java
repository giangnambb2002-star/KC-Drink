package com.example.datn.topping.repository;

import com.example.datn.topping.entity.LoTopping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoToppingRepository extends JpaRepository<LoTopping, Integer> {

    @Query("SELECT l FROM LoTopping l WHERE " +
            "(:trangThai IS NULL OR l.trangThai = :trangThai) AND " +
            "(:idTopping IS NULL OR l.topping.idTopping = :idTopping)")
    Page<LoTopping> searchLoTopping(
            @Param("idTopping") Integer idTopping,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );

    // Vũ khí FEFO dùng cho lúc đặt hàng thanh toán
    @Query("SELECT l FROM LoTopping l WHERE l.topping.idTopping = :idTopping " +
            "AND l.soLuongTon > 0 AND l.trangThai = 1 AND l.hanSuDung >= CURRENT_DATE " +
            "ORDER BY l.hanSuDung ASC")
    List<LoTopping> findLoToUseFEFO(@Param("idTopping") Integer idTopping);
}