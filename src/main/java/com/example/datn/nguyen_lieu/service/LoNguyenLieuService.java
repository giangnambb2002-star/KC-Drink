package com.example.datn.nguyen_lieu.service;

import com.example.datn.common.PageResponse;
import com.example.datn.nguyen_lieu.dto.LoNguyenLieuRequest;
import com.example.datn.nguyen_lieu.dto.LoNguyenLieuResponse;
import com.example.datn.nguyen_lieu.entity.LoNguyenLieu;
import com.example.datn.nguyen_lieu.entity.NguyenLieu;
import com.example.datn.nguyen_lieu.repository.LoNguyenLieuRepository;
import com.example.datn.nguyen_lieu.repository.NguyenLieuRepository;
// Bổ sung import nhân viên (Bro nhớ check lại đúng đường dẫn package nhé)
import com.example.datn.nhan_vien.entity.NhanVien;
import com.example.datn.nhan_vien.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoNguyenLieuService {

    private final LoNguyenLieuRepository repository;
    private final NguyenLieuRepository nguyenLieuRepository;
    // BỔ SUNG: Inject NhanVienRepository để móc data nhân viên
    private final NhanVienRepository nhanVienRepository;

    public PageResponse<LoNguyenLieuResponse> getAll(
            Integer idNguyenLieu, Integer trangThai, int page, int size, String sortBy, String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<LoNguyenLieu> pageData = repository.searchLoNguyenLieu(idNguyenLieu, trangThai, pageable);

        return new PageResponse<>(
                pageData.getContent().stream().map(this::toResponse).toList(),
                pageData.getNumber(), pageData.getSize(), pageData.getTotalElements(),
                pageData.getTotalPages(), pageData.isLast()
        );
    }

    public LoNguyenLieuResponse create(LoNguyenLieuRequest request) {
        // 1. Check nguyên liệu
        NguyenLieu nguyenLieu = nguyenLieuRepository.findById(request.getIdNguyenLieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu gốc"));

        // 2. Check nhân viên nhập kho
        NhanVien nhanVien = nhanVienRepository.findById(request.getIdNhanVien())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin nhân viên nhập kho"));

        LoNguyenLieu lo = new LoNguyenLieu();
        lo.setNguyenLieu(nguyenLieu);
        lo.setMaLo(request.getMaLo());
        lo.setNhanVien(nhanVien); // Gắn người nhập vào lô
        lo.setSoLuongTon(request.getSoLuongTon());
        lo.setHanSuDung(request.getHanSuDung());
        lo.setNgayNhap(LocalDateTime.now());
        lo.setTrangThai(1);

        return toResponse(repository.save(lo));
    }

    // Nghiệp vụ Kho hạn chế sửa số lượng lung tung, chỉ cho phép khóa lô nếu hỏng/hết hạn
    public LoNguyenLieuResponse updateStatus(Integer id, Integer trangThai) {
        LoNguyenLieu lo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lô nguyên liệu"));
        lo.setTrangThai(trangThai);
        return toResponse(repository.save(lo));
    }

    private LoNguyenLieuResponse toResponse(LoNguyenLieu lo) {
        LoNguyenLieuResponse response = new LoNguyenLieuResponse();
        response.setIdLo(lo.getIdLo());
        response.setMaLo(lo.getMaLo());
        response.setIdNguyenLieu(lo.getNguyenLieu().getIdNguyenLieu());
        response.setTenNguyenLieu(lo.getNguyenLieu().getTenNguyenLieu());
        response.setDonViTinh(lo.getNguyenLieu().getDonViTinh());
        response.setSoLuongTon(lo.getSoLuongTon());
        response.setHanSuDung(lo.getHanSuDung());
        response.setNgayNhap(lo.getNgayNhap());
        response.setTrangThai(lo.getTrangThai());

        // BỔ SUNG: Trả về tên nhân viên nhập để Frontend hiển thị lên Table
        if (lo.getNhanVien() != null) {
            response.setTenNhanVien(lo.getNhanVien().getTenNhanVien());
        }

        return response;
    }
}