package com.example.datn.nguyen_lieu.service;

import com.example.datn.common.PageResponse;
import com.example.datn.nguyen_lieu.dto.CongThucSanPhamRequest;
import com.example.datn.nguyen_lieu.dto.CongThucToppingRequest;
import com.example.datn.nguyen_lieu.entity.CongThucSanPham;
import com.example.datn.nguyen_lieu.entity.CongThucTopping;
import com.example.datn.nguyen_lieu.entity.NguyenLieu;
import com.example.datn.nguyen_lieu.repository.CongThucSanPhamRepository;
import com.example.datn.nguyen_lieu.repository.CongThucToppingRepository;
import com.example.datn.nguyen_lieu.repository.NguyenLieuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CongThucService {

    private final CongThucSanPhamRepository ctspRepository;
    private final CongThucToppingRepository cttRepository;
    private final NguyenLieuRepository nguyenLieuRepository;

    // --- PHẦN CÔNG THỨC SẢN PHẨM ---
    public CongThucSanPham createCtsp(CongThucSanPhamRequest request) {
        NguyenLieu nl = nguyenLieuRepository.findById(request.getIdNguyenLieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu"));
        if (nl.getTrangThai() == null || nl.getTrangThai() != 1) {
            throw new RuntimeException("Nguyên liệu đang ngừng sử dụng");
        }

        if (ctspRepository.existsByIdSanPhamAndIdSizeAndNguyenLieu_IdNguyenLieu(
                request.getIdSanPham(),
                request.getIdSize(),
                request.getIdNguyenLieu()
        )) {
            throw new RuntimeException("Nguyên liệu đã tồn tại trong công thức");
        }
        CongThucSanPham ct = new CongThucSanPham();
        ct.setIdSanPham(request.getIdSanPham());
        ct.setIdSize(request.getIdSize());
        ct.setNguyenLieu(nl);
        ct.setSoLuongCanDung(request.getSoLuongCanDung());
        return ctspRepository.save(ct);
    }


    public CongThucSanPham updateCtsp(
            Integer id,
            CongThucSanPhamRequest request) {

        CongThucSanPham ct = ctspRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức"));

        NguyenLieu nl = nguyenLieuRepository.findById(request.getIdNguyenLieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu"));

        if (nl.getTrangThai() == null || nl.getTrangThai() != 1) {
            throw new RuntimeException("Nguyên liệu đang ngừng sử dụng");
        }
        if (ctspRepository.existsByIdSanPhamAndIdSizeAndNguyenLieu_IdNguyenLieuAndIdCtspNot(
                request.getIdSanPham(),
                request.getIdSize(),
                request.getIdNguyenLieu(),
                id
        )) {
            throw new RuntimeException("Nguyên liệu đã tồn tại trong công thức");
        }
        ct.setIdSanPham(request.getIdSanPham());
        ct.setIdSize(request.getIdSize());
        ct.setNguyenLieu(nl);
        ct.setSoLuongCanDung(request.getSoLuongCanDung());

        return ctspRepository.save(ct);
    }

    public void deleteCtsp(Integer id) {
        CongThucSanPham ct = ctspRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức"));

        ctspRepository.delete(ct);
    }

    public PageResponse<CongThucSanPham> getBySanPhamAndSize(
            Integer idSanPham, Integer idSize, int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CongThucSanPham> pageData = ctspRepository.findByIdSanPhamAndIdSize(idSanPham, idSize, pageable);

        return new PageResponse<>(
                pageData.getContent(),
                pageData.getNumber(), pageData.getSize(), pageData.getTotalElements(),
                pageData.getTotalPages(), pageData.isLast()
        );
    }

    // --- PHẦN CÔNG THỨC TOPPING ---
    public CongThucTopping createCtt(CongThucToppingRequest request) {
        NguyenLieu nl = nguyenLieuRepository.findById(request.getIdNguyenLieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu"));

        CongThucTopping ct = new CongThucTopping();
        ct.setIdTopping(request.getIdTopping());
        ct.setNguyenLieu(nl);
        ct.setSoLuongCanDung(request.getSoLuongCanDung());
        return cttRepository.save(ct);
    }

    public PageResponse<CongThucTopping> getByTopping(
            Integer idTopping, int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CongThucTopping> pageData = cttRepository.findByIdTopping(idTopping, pageable);

        return new PageResponse<>(
                pageData.getContent(),
                pageData.getNumber(), pageData.getSize(), pageData.getTotalElements(),
                pageData.getTotalPages(), pageData.isLast()
        );
    }
}