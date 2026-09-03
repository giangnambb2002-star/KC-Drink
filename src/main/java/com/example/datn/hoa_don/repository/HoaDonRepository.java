package com.example.datn.hoa_don.repository;

import com.example.datn.hoa_don.entity.HoaDon;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {

    Optional<HoaDon> findByMaHoaDon(String maHoaDon);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT h FROM HoaDon h WHERE h.idHoaDon = :idHoaDon")
    Optional<HoaDon> findByIdForUpdate(
            @Param("idHoaDon") Integer idHoaDon
    );
}