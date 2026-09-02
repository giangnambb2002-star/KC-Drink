package com.example.datn.ban_thanh_pham.service;

import com.example.datn.ban_thanh_pham.dto.BanThanhPhamRequest;
import com.example.datn.ban_thanh_pham.dto.BanThanhPhamResponse;
import com.example.datn.ban_thanh_pham.entity.BanThanhPham;
import com.example.datn.ban_thanh_pham.entity.MePhaChe;
import com.example.datn.ban_thanh_pham.repository.BanThanhPhamRepository;
import com.example.datn.ban_thanh_pham.repository.MePhaCheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BanThanhPhamService {

    private final BanThanhPhamRepository repository;
    private final MePhaCheRepository mePhaCheRepository;

    public List<BanThanhPhamResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public BanThanhPhamResponse getById(Integer id) {
        return toResponse(findById(id));
    }

    public BanThanhPhamResponse create(BanThanhPhamRequest request) {
        if (repository.existsByTenBanThanhPhamIgnoreCase(request.getTenBanThanhPham().trim())) {
            throw new RuntimeException("Tên bán thành phẩm đã tồn tại");
        }

        BanThanhPham banThanhPham = new BanThanhPham();
        banThanhPham.setTenBanThanhPham(request.getTenBanThanhPham().trim());
        banThanhPham.setDonViTinh(request.getDonViTinh().trim());
        banThanhPham.setHanSuDungGio(request.getHanSuDungGio());
        banThanhPham.setTrangThai(1);

        return toResponse(repository.save(banThanhPham));
    }

    public BanThanhPhamResponse update(Integer id, BanThanhPhamRequest request) {
        BanThanhPham banThanhPham = findById(id);

        if (!banThanhPham.getTenBanThanhPham().equalsIgnoreCase(request.getTenBanThanhPham().trim())
                && repository.existsByTenBanThanhPhamIgnoreCase(request.getTenBanThanhPham().trim())) {
            throw new RuntimeException("Tên bán thành phẩm đã tồn tại");
        }

        banThanhPham.setTenBanThanhPham(request.getTenBanThanhPham().trim());
        banThanhPham.setDonViTinh(request.getDonViTinh().trim());
        banThanhPham.setHanSuDungGio(request.getHanSuDungGio());

        return toResponse(repository.save(banThanhPham));
    }

    public BanThanhPhamResponse lock(Integer id) {
        BanThanhPham banThanhPham = findById(id);
        banThanhPham.setTrangThai(0);
        return toResponse(repository.save(banThanhPham));
    }

    public BanThanhPhamResponse unlock(Integer id) {
        BanThanhPham banThanhPham = findById(id);
        banThanhPham.setTrangThai(1);
        return toResponse(repository.save(banThanhPham));
    }

    private BanThanhPham findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bán thành phẩm"));
    }

    private BanThanhPhamResponse toResponse(BanThanhPham banThanhPham) {
        BanThanhPhamResponse response = new BanThanhPhamResponse();

        response.setIdBanThanhPham(banThanhPham.getIdBanThanhPham());
        response.setTenBanThanhPham(banThanhPham.getTenBanThanhPham());
        response.setDonViTinh(banThanhPham.getDonViTinh());
        response.setHanSuDungGio(banThanhPham.getHanSuDungGio());
        response.setTrangThai(banThanhPham.getTrangThai());
        response.setTongTon(tinhTongTon(banThanhPham.getIdBanThanhPham()));

        return response;
    }

    private BigDecimal tinhTongTon(Integer idBanThanhPham) {
        LocalDateTime now = LocalDateTime.now();

        List<MePhaChe> danhSachMe =
                mePhaCheRepository.findByBanThanhPham_IdBanThanhPhamAndTrangThaiOrderByNgayPhaAsc(
                        idBanThanhPham, 1
                );

        return danhSachMe.stream()
                .filter(me -> me.getSoLuongConLai() != null)
                .filter(me -> me.getSoLuongConLai().compareTo(BigDecimal.ZERO) > 0)
                .filter(me -> me.getHanSuDung() == null || me.getHanSuDung().isAfter(now))
                .map(MePhaChe::getSoLuongConLai)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}