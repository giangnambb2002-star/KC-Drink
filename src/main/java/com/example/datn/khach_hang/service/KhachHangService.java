package com.example.datn.khach_hang.service;

import com.example.datn.khach_hang.dto.KhachHangRequest;
import com.example.datn.khach_hang.dto.KhachHangResponse;
import com.example.datn.khach_hang.entity.KhachHang;
import com.example.datn.khach_hang.repository.KhachHangRepository;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import com.example.datn.tai_khoan.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KhachHangService {

    private final KhachHangRepository repository;
    private final TaiKhoanRepository taiKhoanRepository;

    public List<KhachHangResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public KhachHangResponse getById(Integer id) {

        KhachHang khachHang = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        return toResponse(khachHang);
    }
    public KhachHangResponse create(KhachHangRequest request) {

        TaiKhoan taiKhoan = null;
        if (request.getIdTaiKhoan() != null) {
            taiKhoan = taiKhoanRepository.findById(request.getIdTaiKhoan())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        }
        KhachHang khachHang = new KhachHang();

        khachHang.setTenKhachHang(request.getTenKhachHang());
        khachHang.setSdt(request.getSdt());
        khachHang.setEmail(request.getEmail());
        khachHang.setDiaChi(request.getDiaChi());
        khachHang.setGioiTinh(request.getGioiTinh());
        khachHang.setNgaySinh(request.getNgaySinh());
        khachHang.setDiemTichLuy(
                request.getDiemTichLuy() == null ? 0 : request.getDiemTichLuy()
        );
        khachHang.setTaiKhoan(taiKhoan);

        return toResponse(repository.save(khachHang));
    }
    public KhachHangResponse update(Integer id, KhachHangRequest request) {

        KhachHang khachHang = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        TaiKhoan taiKhoan = null;
        if (request.getIdTaiKhoan() != null) {
            taiKhoan = taiKhoanRepository.findById(request.getIdTaiKhoan())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        }
        khachHang.setTenKhachHang(request.getTenKhachHang());
        khachHang.setSdt(request.getSdt());
        khachHang.setEmail(request.getEmail());
        khachHang.setDiaChi(request.getDiaChi());
        khachHang.setGioiTinh(request.getGioiTinh());
        khachHang.setNgaySinh(request.getNgaySinh());
        khachHang.setDiemTichLuy(
                request.getDiemTichLuy() == null ? 0 : request.getDiemTichLuy()
        );
        khachHang.setTaiKhoan(taiKhoan);

        return toResponse(repository.save(khachHang));
    }
    public KhachHangResponse lock(Integer id) {
        KhachHang khachHang = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        khachHang.setTrangThai(0);

        return toResponse(repository.save(khachHang));
    }

    public KhachHangResponse unlock(Integer id) {
        KhachHang khachHang = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        khachHang.setTrangThai(1);

        return toResponse(repository.save(khachHang));
    }
    private KhachHangResponse toResponse(KhachHang khachHang) {

        KhachHangResponse response = new KhachHangResponse();

        response.setIdKhachHang(khachHang.getIdKhachHang());
        response.setTenKhachHang(khachHang.getTenKhachHang());
        response.setSdt(khachHang.getSdt());
        response.setEmail(khachHang.getEmail());
        response.setDiaChi(khachHang.getDiaChi());
        response.setGioiTinh(khachHang.getGioiTinh());
        response.setNgaySinh(khachHang.getNgaySinh());
        response.setTrangThai(khachHang.getTrangThai());
        response.setDiemTichLuy(khachHang.getDiemTichLuy());

        if (khachHang.getTaiKhoan() != null) {
            response.setIdTaiKhoan(
                    khachHang.getTaiKhoan().getIdTaiKhoan()
            );
            response.setUsername(
                    khachHang.getTaiKhoan().getUsername()
            );
        }

        return response;
    }
}