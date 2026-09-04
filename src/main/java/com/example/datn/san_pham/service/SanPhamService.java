package com.example.datn.san_pham.service;

import com.example.datn.common.PageResponse;
import com.example.datn.san_pham.dto.SanPhamResponse;
import com.example.datn.san_pham.entity.SanPham;
import com.example.datn.san_pham.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.core.io.Resource;
import com.example.datn.khuyen_mai.dto.KetQuaKhuyenMaiResponse;
import com.example.datn.khuyen_mai.service.KhuyenMaiService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.example.datn.san_pham.dto.SanPhamRequest;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SanPhamService {

    private final SanPhamRepository repository;
    private final SanPhamImageStorageService imageStorageService;
    private final KhuyenMaiService khuyenMaiService;



    public PageResponse<SanPhamResponse> getAll(
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
        Page<SanPham> pageData = repository.searchSanPham(keyword, trangThai, pageable);

        return new PageResponse<>(
                pageData.getContent().stream().map(this::toResponse).toList(),
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.isLast()
        );
    }
    public SanPhamResponse create(SanPhamRequest request) {
        String tenSanPham = request.getTenSanPham().trim();
        if (repository.existsByTenSanPhamIgnoreCase(tenSanPham)) {
            throw new RuntimeException("Tên sản phẩm đã tồn tại");
        }
        SanPham sanPham = new SanPham();
        sanPham.setTenSanPham(tenSanPham);
        sanPham.setGia(request.getGia());
        sanPham.setMoTa(request.getMoTa());
        sanPham.setIdDanhMuc(request.getIdDanhMuc());
        sanPham.setTrangThai(1);
        sanPham.setNgayTao(LocalDateTime.now());

        return toResponse(repository.save(sanPham));
    }

    public SanPhamResponse update(Integer id, SanPhamRequest request) {
        SanPham sanPham = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        String tenSanPham = request.getTenSanPham().trim();
        if (repository.existsByTenSanPhamIgnoreCaseAndIdSanPhamNot(
                tenSanPham,
                id
        )) {
            throw new RuntimeException("Tên sản phẩm đã tồn tại");
        }

        sanPham.setTenSanPham(tenSanPham);
        sanPham.setGia(request.getGia());
        sanPham.setMoTa(request.getMoTa());
        sanPham.setIdDanhMuc(request.getIdDanhMuc());

        return toResponse(repository.save(sanPham));
    }

    public SanPhamResponse lock(Integer id) {
        SanPham sanPham = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        sanPham.setTrangThai(0);
        return toResponse(repository.save(sanPham));
    }

    public SanPhamResponse unlock(Integer id) {
        SanPham sanPham = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        sanPham.setTrangThai(1);
        return toResponse(repository.save(sanPham));
    }

    public PageResponse<SanPhamResponse> getSanPhamDangBan(
            String keyword,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        String keywordChuanHoa = keyword == null
                ? null
                : keyword.trim();

        Page<SanPham> pageData = repository.searchSanPham(
                keywordChuanHoa,
                1,
                pageable
        );

        List<SanPhamResponse> content = pageData.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                content,
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.isLast()
        );
    }
    public SanPhamResponse getById(Integer id) {
        SanPham sanPham = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        return toResponse(sanPham);
    }

    private SanPhamResponse toResponse(
            SanPham sanPham
    ) {
        KetQuaKhuyenMaiResponse ketQua =
                khuyenMaiService.tinhKhuyenMaiChoSanPham(
                        sanPham.getIdSanPham()
                );

        SanPhamResponse response = new SanPhamResponse();

        response.setIdSanPham(
                sanPham.getIdSanPham()
        );
        response.setTenSanPham(
                sanPham.getTenSanPham()
        );

        // Luôn giữ giá gốc trong trường gia
        response.setGia(sanPham.getGia());

        response.setMoTa(sanPham.getMoTa());
        response.setHinhAnh(sanPham.getHinhAnh());
        response.setTrangThai(sanPham.getTrangThai());
        response.setIdDanhMuc(sanPham.getIdDanhMuc());

        response.setIdKm(ketQua.getIdKm());
        response.setTenKhuyenMai(
                ketQua.getTenKm()
        );
        response.setTienGiamKhuyenMai(
                ketQua.getTienGiam()
        );
        response.setGiaSauKhuyenMai(
                ketQua.getGiaSauKhuyenMai()
        );
        response.setCoKhuyenMai(
                ketQua.getIdKm() != null
                        && ketQua.getTienGiam()
                        .compareTo(java.math.BigDecimal.ZERO) > 0
        );

        return response;
    }
    @Transactional
    public SanPhamResponse uploadHinhAnh(
            Integer id,
            MultipartFile file
    ) {
        SanPham sanPham = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy sản phẩm"
                        )
                );

        String hinhAnhCu = sanPham.getHinhAnh();
        String hinhAnhMoi = imageStorageService.save(file);

        try {
            sanPham.setHinhAnh(hinhAnhMoi);

            SanPham daLuu = repository.saveAndFlush(sanPham);
            imageStorageService.deleteByUrl(hinhAnhCu);

            return toResponse(daLuu);
        } catch (RuntimeException exception) {
            imageStorageService.deleteByUrl(hinhAnhMoi);
            throw exception;
        }
    }

    @Transactional
    public SanPhamResponse deleteHinhAnh(Integer id) {
        SanPham sanPham = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy sản phẩm"
                        )
                );

        String hinhAnhCu = sanPham.getHinhAnh();
        sanPham.setHinhAnh(null);

        SanPham daLuu = repository.saveAndFlush(sanPham);
        imageStorageService.deleteByUrl(hinhAnhCu);

        return toResponse(daLuu);
    }

    public Resource loadHinhAnh(String fileName) {
        return imageStorageService.load(fileName);
    }
}