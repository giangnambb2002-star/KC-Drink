package com.example.datn.nhan_vien.service;

import com.example.datn.nhan_vien.dto.NhanVienRequest;
import com.example.datn.nhan_vien.entity.NhanVien;
import com.example.datn.nhan_vien.repository.NhanVienRepository;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import com.example.datn.tai_khoan.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NhanVienService {

    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    public List<NhanVien> getAll() {
        return nhanVienRepository.findAll();
    }

    public NhanVien getById(Integer id) {
        return nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
    }

    public NhanVien create(NhanVienRequest request) {

        TaiKhoan taiKhoan = taiKhoanRepository.findById(request.getIdTaiKhoan())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        NhanVien nhanVien = new NhanVien();

        nhanVien.setTenNhanVien(request.getTenNhanVien());
        nhanVien.setSdt(request.getSdt());
        nhanVien.setEmail(request.getEmail());
        nhanVien.setChucVu(request.getChucVu());
        nhanVien.setTaiKhoan(taiKhoan);

        return nhanVienRepository.save(nhanVien);
    }

    public NhanVien update(Integer id, NhanVienRequest request) {

        NhanVien nhanVien = getById(id);

        TaiKhoan taiKhoan = taiKhoanRepository.findById(request.getIdTaiKhoan())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        nhanVien.setTenNhanVien(request.getTenNhanVien());
        nhanVien.setSdt(request.getSdt());
        nhanVien.setEmail(request.getEmail());
        nhanVien.setChucVu(request.getChucVu());
        nhanVien.setTaiKhoan(taiKhoan);

        return nhanVienRepository.save(nhanVien);
    }

    public void delete(Integer id) {

        if (!nhanVienRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy nhân viên");
        }

        nhanVienRepository.deleteById(id);
    }
}