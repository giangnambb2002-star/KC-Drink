package com.example.datn.ban_thanh_pham.service;

import com.example.datn.ban_thanh_pham.dto.MePhaCheResponse;
import com.example.datn.ban_thanh_pham.dto.TaoMePhaCheRequest;
import com.example.datn.ban_thanh_pham.entity.BanThanhPham;
import com.example.datn.ban_thanh_pham.entity.CongThucBanThanhPham;
import com.example.datn.ban_thanh_pham.entity.MePhaChe;
import com.example.datn.ban_thanh_pham.repository.BanThanhPhamRepository;
import com.example.datn.ban_thanh_pham.repository.CongThucBanThanhPhamRepository;
import com.example.datn.ban_thanh_pham.repository.MePhaCheRepository;
import com.example.datn.nguyen_lieu.service.KhoService;
import com.example.datn.nhan_vien.entity.NhanVien;
import com.example.datn.nhan_vien.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MePhaCheService {

    private final MePhaCheRepository repository;
    private final BanThanhPhamRepository banThanhPhamRepository;
    private final CongThucBanThanhPhamRepository congThucRepository;
    private final NhanVienRepository nhanVienRepository;
    private final KhoService khoService;

    public List<MePhaCheResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MePhaCheResponse getById(Integer id) {
        MePhaChe mePhaChe = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mẻ pha"));
        return toResponse(mePhaChe);
    }

    @Transactional
    public MePhaCheResponse create(TaoMePhaCheRequest request) {
        BanThanhPham banThanhPham = banThanhPhamRepository.findById(request.getIdBanThanhPham())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bán thành phẩm"));

        if (banThanhPham.getTrangThai() == null || banThanhPham.getTrangThai() != 1) {
            throw new RuntimeException("Bán thành phẩm đang ngừng hoạt động");
        }

        List<CongThucBanThanhPham> congThucList =
                congThucRepository.findByBanThanhPham_IdBanThanhPham(request.getIdBanThanhPham());

        if (congThucList.isEmpty()) {
            throw new RuntimeException("Bán thành phẩm chưa có công thức");
        }

        NhanVien nhanVien = null;
        if (request.getIdNhanVien() != null) {
            nhanVien = nhanVienRepository.findById(request.getIdNhanVien())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        }

        for (CongThucBanThanhPham congThuc : congThucList) {
            if (congThuc.getSoLuongThanhPham() == null
                    || congThuc.getSoLuongThanhPham().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Công thức bán thành phẩm không hợp lệ");
            }

            BigDecimal tiLe = request.getSoLuongTaoRa()
                    .divide(congThuc.getSoLuongThanhPham(), 6, BigDecimal.ROUND_HALF_UP);

            BigDecimal soLuongCanDung = congThuc.getSoLuongNguyenLieu()
                    .multiply(tiLe);

            khoService.truKhoNguyenLieu(
                    congThuc.getNguyenLieu().getIdNguyenLieu(),
                    soLuongCanDung.doubleValue()
            );
        }

        LocalDateTime ngayPha = LocalDateTime.now();

        MePhaChe mePhaChe = new MePhaChe();
        mePhaChe.setBanThanhPham(banThanhPham);
        mePhaChe.setNhanVien(nhanVien);
        mePhaChe.setSoLuongTaoRa(request.getSoLuongTaoRa());
        mePhaChe.setSoLuongConLai(request.getSoLuongTaoRa());
        mePhaChe.setNgayPha(ngayPha);

        if (banThanhPham.getHanSuDungGio() != null) {
            mePhaChe.setHanSuDung(
                    ngayPha.plusHours(banThanhPham.getHanSuDungGio())
            );
        }

        mePhaChe.setTrangThai(1);
        mePhaChe.setGhiChu(request.getGhiChu());

        return toResponse(repository.save(mePhaChe));
    }

    private MePhaCheResponse toResponse(MePhaChe mePhaChe) {
        MePhaCheResponse response = new MePhaCheResponse();

        response.setIdMePha(mePhaChe.getIdMePha());

        response.setIdBanThanhPham(
                mePhaChe.getBanThanhPham().getIdBanThanhPham()
        );
        response.setTenBanThanhPham(
                mePhaChe.getBanThanhPham().getTenBanThanhPham()
        );
        response.setDonViTinh(
                mePhaChe.getBanThanhPham().getDonViTinh()
        );

        if (mePhaChe.getNhanVien() != null) {
            response.setIdNhanVien(mePhaChe.getNhanVien().getIdNhanVien());
            response.setTenNhanVien(mePhaChe.getNhanVien().getTenNhanVien());
        }

        response.setSoLuongTaoRa(mePhaChe.getSoLuongTaoRa());
        response.setSoLuongConLai(mePhaChe.getSoLuongConLai());
        response.setNgayPha(mePhaChe.getNgayPha());
        response.setHanSuDung(mePhaChe.getHanSuDung());
        response.setTrangThai(mePhaChe.getTrangThai());
        response.setGhiChu(mePhaChe.getGhiChu());

        return response;
    }
}