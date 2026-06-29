package com.example.datn.nhan_vien.repository;

import com.example.datn.nhan_vien.entity.NhanVien;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {

    Optional<NhanVien> findByTaiKhoan(TaiKhoan taiKhoan);
    Page<NhanVien> findByTenNhanVienContainingIgnoreCaseOrSdtContaining(
            String ten,
            String sdt,
            Pageable pageable
    );
    Optional<NhanVien> findBySdt(String sdt);
    Optional<NhanVien> findByEmail(String email);

    Page<NhanVien> findByChucVu(
            String chucVu,
            Pageable pageable
    );

    Page<NhanVien> findByTrangThai(
            Integer trangThai,
            Pageable pageable
    );

    Page<NhanVien> findByChucVuAndTrangThai(
            String chucVu,
            Integer trangThai,
            Pageable pageable
    );

    @Query("""
SELECT nv
FROM NhanVien nv
WHERE
(:keyword IS NULL
 OR LOWER(nv.tenNhanVien) LIKE LOWER(CONCAT('%', :keyword, '%'))
 OR nv.sdt LIKE CONCAT('%', :keyword, '%'))
AND
(:chucVu IS NULL
 OR nv.chucVu = :chucVu)
AND
(:trangThai IS NULL
 OR nv.trangThai = :trangThai)
""")
    Page<NhanVien> search(
            @Param("keyword") String keyword,
            @Param("chucVu") String chucVu,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );
}