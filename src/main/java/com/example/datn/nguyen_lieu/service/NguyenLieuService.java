package com.example.datn.nguyen_lieu.service;

import com.example.datn.common.PageResponse;
import com.example.datn.nguyen_lieu.dto.NguyenLieuRequest;
import com.example.datn.nguyen_lieu.dto.NguyenLieuResponse;
import com.example.datn.nguyen_lieu.entity.NguyenLieu;
import com.example.datn.nguyen_lieu.repository.LoNguyenLieuRepository;
import com.example.datn.nguyen_lieu.repository.NguyenLieuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class NguyenLieuService {

    private final NguyenLieuRepository repository;

    private final LoNguyenLieuRepository loNguyenLieuRepository;

    // Phân trang + Tìm kiếm + Sắp xếp y hệt Leader
    public PageResponse<NguyenLieuResponse> getAll(
            String keyword,
            Integer trangThai,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<NguyenLieu> pageData = repository.searchNguyenLieu(keyword, trangThai, pageable);

        return new PageResponse<>(
                pageData.getContent().stream().map(this::toResponse).toList(),
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.isLast()
        );
    }

    public NguyenLieuResponse getById(Integer id) {
        NguyenLieu nguyenLieu = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu"));
        return toResponse(nguyenLieu);
    }

    public NguyenLieuResponse create(NguyenLieuRequest request) {
        if (repository.existsByTenNguyenLieu(request.getTenNguyenLieu().trim())) {
            throw new RuntimeException("Tên nguyên liệu này đã tồn tại trong kho!");
        }
        NguyenLieu nguyenLieu = new NguyenLieu();
        nguyenLieu.setTenNguyenLieu(request.getTenNguyenLieu());
        nguyenLieu.setDonViTinh(request.getDonViTinh());
        nguyenLieu.setTrangThai(1);
        nguyenLieu.setNguongTonKho(request.getNguongTonKho());
        return toResponse(repository.save(nguyenLieu));
    }

    public NguyenLieuResponse update(Integer id, NguyenLieuRequest request) {
        NguyenLieu nguyenLieu = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu"));
        if (repository.existsByTenNguyenLieuAndIdNguyenLieuNot(request.getTenNguyenLieu().trim(), id)) {
            throw new RuntimeException("Tên nguyên liệu này đã bị trùng với một nguyên liệu khác!");
        }
        nguyenLieu.setTenNguyenLieu(request.getTenNguyenLieu());
        nguyenLieu.setDonViTinh(request.getDonViTinh());
        nguyenLieu.setNguongTonKho(request.getNguongTonKho());
        if (request.getTrangThai() != null) {
            nguyenLieu.setTrangThai(request.getTrangThai());
        }
        return toResponse(repository.save(nguyenLieu));
    }

    // Khóa nguyên liệu (Ngừng sử dụng)
    public NguyenLieuResponse lock(Integer id) {
        NguyenLieu nguyenLieu = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu"));
        nguyenLieu.setTrangThai(0);
        return toResponse(repository.save(nguyenLieu));
    }

    // Mở khóa nguyên liệu (Sử dụng lại)
    public NguyenLieuResponse unlock(Integer id) {
        NguyenLieu nguyenLieu = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu"));
        nguyenLieu.setTrangThai(1);
        return toResponse(repository.save(nguyenLieu));
    }

    // Hàm chuyển Entity thành Response DTO
    private NguyenLieuResponse toResponse(NguyenLieu nguyenLieu) {
        NguyenLieuResponse response = new NguyenLieuResponse();
        response.setIdNguyenLieu(nguyenLieu.getIdNguyenLieu());
        response.setTenNguyenLieu(nguyenLieu.getTenNguyenLieu());
        response.setDonViTinh(nguyenLieu.getDonViTinh());
        response.setTrangThai(nguyenLieu.getTrangThai());
        BigDecimal tongTonKhoConHan = loNguyenLieuRepository.getTongTonKhoConHan(nguyenLieu.getIdNguyenLieu());
        response.setTongTonKho(tongTonKhoConHan != null ? tongTonKhoConHan : BigDecimal.ZERO);
        response.setNguongTonKho(nguyenLieu.getNguongTonKho());
        return response;
    }
}