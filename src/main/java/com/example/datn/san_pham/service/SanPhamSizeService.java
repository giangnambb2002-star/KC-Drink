package com.example.datn.san_pham.service;

import com.example.datn.san_pham.dto.SanPhamSizeRequest;
import com.example.datn.san_pham.dto.SanPhamSizeResponse;
import com.example.datn.san_pham.entity.SanPhamSize;
import com.example.datn.san_pham.entity.Size;
import com.example.datn.san_pham.repository.SanPhamSizeRepository;
import com.example.datn.san_pham.repository.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SanPhamSizeService {

    private final SanPhamSizeRepository repository;
    private final SizeRepository sizeRepository;

    public List<SanPhamSizeResponse> getBySanPham(Integer idSanPham) {
        return repository.findByIdSanPham(idSanPham).stream()
                .map(this::toResponse)
                .sorted((a, b) -> a.getThuTu().compareTo(b.getThuTu()))
                .toList();
    }

    public SanPhamSizeResponse create(SanPhamSizeRequest request) {
        Size size = sizeRepository.findById(request.getIdSize())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy size"));

        if (repository.existsByIdSanPhamAndSize_IdSize(
                request.getIdSanPham(), request.getIdSize())) {
            throw new RuntimeException("Sản phẩm đã có size này");
        }

        SanPhamSize sanPhamSize = new SanPhamSize();
        sanPhamSize.setIdSanPham(request.getIdSanPham());
        sanPhamSize.setSize(size);
        sanPhamSize.setPhuThu(
                request.getPhuThu() == null
                        ? BigDecimal.ZERO
                        : request.getPhuThu()
        );

        return toResponse(repository.save(sanPhamSize));
    }

    public SanPhamSizeResponse update(
            Integer id,
            SanPhamSizeRequest request) {

        SanPhamSize sanPhamSize = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy size của sản phẩm"));

        if (request.getPhuThu() != null
                && request.getPhuThu().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Phụ thu không được nhỏ hơn 0");
        }

        sanPhamSize.setPhuThu(
                request.getPhuThu() == null
                        ? BigDecimal.ZERO
                        : request.getPhuThu()
        );

        return toResponse(repository.save(sanPhamSize));
    }

    public void delete(Integer id) {
        SanPhamSize sanPhamSize = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy size của sản phẩm"));

        repository.delete(sanPhamSize);
    }

    private SanPhamSizeResponse toResponse(SanPhamSize sanPhamSize) {
        SanPhamSizeResponse response = new SanPhamSizeResponse();
        response.setId(sanPhamSize.getId());
        response.setIdSanPham(sanPhamSize.getIdSanPham());
        response.setIdSize(sanPhamSize.getSize().getIdSize());
        response.setTenSize(sanPhamSize.getSize().getTenSize());
        response.setPhuThu(
                sanPhamSize.getPhuThu() == null
                        ? BigDecimal.ZERO
                        : sanPhamSize.getPhuThu()
        );
        response.setThuTu(sanPhamSize.getSize().getThuTu());
        return response;
    }
}