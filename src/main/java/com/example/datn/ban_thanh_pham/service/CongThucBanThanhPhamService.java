package com.example.datn.ban_thanh_pham.service;

import com.example.datn.ban_thanh_pham.dto.CongThucBanThanhPhamRequest;
import com.example.datn.ban_thanh_pham.dto.CongThucBanThanhPhamResponse;
import com.example.datn.ban_thanh_pham.entity.BanThanhPham;
import com.example.datn.ban_thanh_pham.entity.CongThucBanThanhPham;
import com.example.datn.ban_thanh_pham.repository.BanThanhPhamRepository;
import com.example.datn.ban_thanh_pham.repository.CongThucBanThanhPhamRepository;
import com.example.datn.nguyen_lieu.entity.NguyenLieu;
import com.example.datn.nguyen_lieu.repository.NguyenLieuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CongThucBanThanhPhamService {

    private final CongThucBanThanhPhamRepository repository;
    private final BanThanhPhamRepository banThanhPhamRepository;
    private final NguyenLieuRepository nguyenLieuRepository;

    public List<CongThucBanThanhPhamResponse> getByBanThanhPham(Integer idBanThanhPham) {
        return repository.findByBanThanhPham_IdBanThanhPham(idBanThanhPham)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CongThucBanThanhPhamResponse create(CongThucBanThanhPhamRequest request) {
        BanThanhPham banThanhPham = banThanhPhamRepository.findById(request.getIdBanThanhPham())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bán thành phẩm"));

        NguyenLieu nguyenLieu = nguyenLieuRepository.findById(request.getIdNguyenLieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu"));

        CongThucBanThanhPham congThuc = new CongThucBanThanhPham();
        congThuc.setBanThanhPham(banThanhPham);
        congThuc.setNguyenLieu(nguyenLieu);
        congThuc.setSoLuongNguyenLieu(request.getSoLuongNguyenLieu());
        congThuc.setSoLuongThanhPham(request.getSoLuongThanhPham());

        return toResponse(repository.save(congThuc));
    }

    public CongThucBanThanhPhamResponse update(Integer id, CongThucBanThanhPhamRequest request) {
        CongThucBanThanhPham congThuc = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức"));

        BanThanhPham banThanhPham = banThanhPhamRepository.findById(request.getIdBanThanhPham())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bán thành phẩm"));

        NguyenLieu nguyenLieu = nguyenLieuRepository.findById(request.getIdNguyenLieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu"));

        congThuc.setBanThanhPham(banThanhPham);
        congThuc.setNguyenLieu(nguyenLieu);
        congThuc.setSoLuongNguyenLieu(request.getSoLuongNguyenLieu());
        congThuc.setSoLuongThanhPham(request.getSoLuongThanhPham());

        return toResponse(repository.save(congThuc));
    }

    public void delete(Integer id) {
        CongThucBanThanhPham congThuc = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức"));

        repository.delete(congThuc);
    }

    private CongThucBanThanhPhamResponse toResponse(CongThucBanThanhPham congThuc) {
        CongThucBanThanhPhamResponse response = new CongThucBanThanhPhamResponse();

        response.setIdCtBtp(congThuc.getIdCtBtp());

        response.setIdBanThanhPham(congThuc.getBanThanhPham().getIdBanThanhPham());
        response.setTenBanThanhPham(congThuc.getBanThanhPham().getTenBanThanhPham());
        response.setDonViThanhPham(congThuc.getBanThanhPham().getDonViTinh());

        response.setIdNguyenLieu(congThuc.getNguyenLieu().getIdNguyenLieu());
        response.setTenNguyenLieu(congThuc.getNguyenLieu().getTenNguyenLieu());
        response.setDonViNguyenLieu(congThuc.getNguyenLieu().getDonViTinh());

        response.setSoLuongNguyenLieu(congThuc.getSoLuongNguyenLieu());
        response.setSoLuongThanhPham(congThuc.getSoLuongThanhPham());

        return response;
    }
}