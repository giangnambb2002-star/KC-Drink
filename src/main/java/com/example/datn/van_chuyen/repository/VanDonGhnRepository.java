package com.example.datn.van_chuyen.repository;

import com.example.datn.van_chuyen.entity.VanDonGhn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VanDonGhnRepository extends JpaRepository<VanDonGhn, Integer> {
    Optional<VanDonGhn> findByHoaDon_IdHoaDon(Integer idHoaDon);
    boolean existsByHoaDon_IdHoaDon(Integer idHoaDon);
    Optional<VanDonGhn> findByMaVanDonGhn(String maVanDonGhn);
}