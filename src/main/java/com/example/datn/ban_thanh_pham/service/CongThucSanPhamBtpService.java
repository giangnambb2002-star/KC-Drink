package com.example.datn.ban_thanh_pham.service;

import com.example.datn.ban_thanh_pham.dto.CongThucSanPhamBtpRequest;
import com.example.datn.ban_thanh_pham.dto.CongThucSanPhamBtpResponse;
import com.example.datn.ban_thanh_pham.entity.BanThanhPham;
import com.example.datn.ban_thanh_pham.entity.CongThucSanPhamBtp;
import com.example.datn.ban_thanh_pham.repository.BanThanhPhamRepository;
import com.example.datn.ban_thanh_pham.repository.CongThucSanPhamBtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CongThucSanPhamBtpService {

    private final CongThucSanPhamBtpRepository repository;
    private final BanThanhPhamRepository banThanhPhamRepository;

    public List<CongThucSanPhamBtpResponse> getBySanPhamAndSize(
            Integer idSanPham,
            Integer idSize) {
        return repository.findByIdSanPhamAndIdSize(idSanPham, idSize)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CongThucSanPhamBtpResponse create(CongThucSanPhamBtpRequest request) {
        BanThanhPham banThanhPham = banThanhPhamRepository
                .findById(request.getIdBanThanhPham())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bán thành phẩm"));

        if (banThanhPham.getTrangThai() == null || banThanhPham.getTrangThai() != 1) {
            throw new RuntimeException("Bán thành phẩm đang ngừng hoạt động");
        }

        boolean existed =
                repository.existsByIdSanPhamAndIdSizeAndBanThanhPham_IdBanThanhPham(
                        request.getIdSanPham(),
                        request.getIdSize(),
                        request.getIdBanThanhPham()
                );

        if (existed) {
            throw new RuntimeException("Công thức sản phẩm bán thành phẩm đã tồn tại");
        }

        CongThucSanPhamBtp congThuc = new CongThucSanPhamBtp();
        congThuc.setIdSanPham(request.getIdSanPham());
        congThuc.setIdSize(request.getIdSize());
        congThuc.setBanThanhPham(banThanhPham);
        congThuc.setSoLuongCanDung(request.getSoLuongCanDung());

        return toResponse(repository.save(congThuc));
    }

    public CongThucSanPhamBtpResponse update(
            Integer id,
            CongThucSanPhamBtpRequest request) {

        CongThucSanPhamBtp congThuc = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức"));

        BanThanhPham banThanhPham = banThanhPhamRepository
                .findById(request.getIdBanThanhPham())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bán thành phẩm"));

        congThuc.setIdSanPham(request.getIdSanPham());
        congThuc.setIdSize(request.getIdSize());
        congThuc.setBanThanhPham(banThanhPham);
        congThuc.setSoLuongCanDung(request.getSoLuongCanDung());

        return toResponse(repository.save(congThuc));
    }

    public void delete(Integer id) {
        CongThucSanPhamBtp congThuc = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức"));

        repository.delete(congThuc);
    }

    private CongThucSanPhamBtpResponse toResponse(CongThucSanPhamBtp congThuc) {
        CongThucSanPhamBtpResponse response = new CongThucSanPhamBtpResponse();

        response.setId(congThuc.getId());
        response.setIdSanPham(congThuc.getIdSanPham());
        response.setIdSize(congThuc.getIdSize());

        response.setIdBanThanhPham(
                congThuc.getBanThanhPham().getIdBanThanhPham()
        );
        response.setTenBanThanhPham(
                congThuc.getBanThanhPham().getTenBanThanhPham()
        );
        response.setDonViTinh(
                congThuc.getBanThanhPham().getDonViTinh()
        );

        response.setSoLuongCanDung(congThuc.getSoLuongCanDung());

        return response;
    }
}