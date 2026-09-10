package com.example.datn.van_chuyen.repository;

import com.example.datn.van_chuyen.entity.VanDonGhn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;


@Repository
public interface VanDonGhnRepository extends JpaRepository<VanDonGhn, Integer> {
    Optional<VanDonGhn> findByHoaDon_IdHoaDon(Integer idHoaDon);
    boolean existsByHoaDon_IdHoaDon(Integer idHoaDon);
    Optional<VanDonGhn> findByMaVanDonGhn(String maVanDonGhn);
    List<VanDonGhn> findAllByHoaDon_IdHoaDonIn(
            List<Integer> idHoaDonList
    );
    @Query("""
    SELECT COUNT(v.idVanDon)
    FROM VanDonGhn v
    WHERE v.hoaDon.loaiHoaDon = 'ONLINE'
      AND v.trangThai = :trangThai
""")
    long countDonOnlineTheoTrangThai(
            @Param("trangThai") String trangThai
    );
}