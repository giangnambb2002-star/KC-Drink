package com.example.datn.san_pham.service;

import com.example.datn.san_pham.entity.SanPham;
import com.example.datn.san_pham.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SanPhamService {

    private final SanPhamRepository repository;

    public List<SanPham> getAll() {
        return repository.findAll();
    }

    public SanPham getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public SanPham add(SanPham sanPham) {
        return repository.save(sanPham);
    }

    public SanPham update(Integer id, SanPham sanPham) {

        SanPham old = repository.findById(id).orElse(null);

        if (old == null) {
            return null;
        }

        old.setTenSanPham(sanPham.getTenSanPham());
        old.setGia(sanPham.getGia());
        old.setHinhAnh(sanPham.getHinhAnh());
        old.setIdDanhMuc(sanPham.getIdDanhMuc());
        old.setTrangThai(sanPham.getTrangThai());

        return repository.save(old);
    }

    public void delete(Integer id) {

        SanPham sp = repository.findById(id).orElse(null);

        if (sp != null) {
            sp.setTrangThai(false);
            repository.save(sp);
        }
    }

    public boolean isTenSanPhamExist(String tenSanPham) {
        return repository.existsByTenSanPham(tenSanPham);
    }

    public boolean isTenSanPhamExistWhenUpdate(
            String tenSanPham,
            Integer idSanPham) {

        return repository.existsByTenSanPhamAndIdSanPhamNot(
                tenSanPham,
                idSanPham
        );
    }
    public Page<SanPham> search(
            String keyword,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll(pageable);
        }

        return repository.findByTenSanPhamContainingIgnoreCase(
                keyword,
                pageable
        );
    }
}
