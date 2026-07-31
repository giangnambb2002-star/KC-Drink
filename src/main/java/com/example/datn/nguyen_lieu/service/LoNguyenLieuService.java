package com.example.datn.nguyen_lieu.service;

import com.example.datn.common.ExcelHelper;
import com.example.datn.common.PageResponse;
import com.example.datn.nguyen_lieu.dto.LoNguyenLieuRequest;
import com.example.datn.nguyen_lieu.dto.LoNguyenLieuResponse;
import com.example.datn.nguyen_lieu.entity.LoNguyenLieu;
import com.example.datn.nguyen_lieu.entity.NguyenLieu;
import com.example.datn.nguyen_lieu.repository.LoNguyenLieuRepository;
import com.example.datn.nguyen_lieu.repository.NguyenLieuRepository;
import com.example.datn.nhan_vien.entity.NhanVien;
import com.example.datn.nhan_vien.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoNguyenLieuService {

    private final LoNguyenLieuRepository repository;
    private final NguyenLieuRepository nguyenLieuRepository;
    private final NhanVienRepository nhanVienRepository;

    public PageResponse<LoNguyenLieuResponse> getAll(
            Integer idNguyenLieu,
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
        Page<LoNguyenLieu> pageData = repository.searchLoNguyenLieu(
                idNguyenLieu,
                trangThai,
                pageable
        );

        return new PageResponse<>(
                pageData.getContent().stream().map(this::toResponse).toList(),
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.isLast()
        );
    }

    public LoNguyenLieuResponse create(LoNguyenLieuRequest request) {
        NguyenLieu nguyenLieu = nguyenLieuRepository.findById(request.getIdNguyenLieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu gốc"));

        NhanVien nhanVien = nhanVienRepository.findById(request.getIdNhanVien())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin nhân viên nhập kho"));

        LoNguyenLieu lo = new LoNguyenLieu();
        lo.setNguyenLieu(nguyenLieu);
        lo.setMaLo(request.getMaLo());
        lo.setNhanVien(nhanVien);
        lo.setSoLuongTon(request.getSoLuongTon());
        lo.setHanSuDung(request.getHanSuDung());
        lo.setNgayNhap(LocalDateTime.now());
        lo.setTrangThai(1);

        return toResponse(repository.save(lo));
    }

    public LoNguyenLieuResponse lock(Integer id) {
        LoNguyenLieu lo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lô nguyên liệu"));

        lo.setTrangThai(0);
        return toResponse(repository.save(lo));
    }

    public LoNguyenLieuResponse unlock(Integer id) {
        LoNguyenLieu lo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lô nguyên liệu"));

        lo.setTrangThai(1);
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

        if (lo.getNhanVien() != null) {
            response.setTenNhanVien(lo.getNhanVien().getTenNhanVien());
        }

        if (lo.getHanSuDung() != null) {
            LocalDate today = LocalDate.now();
            long days = lo.getHanSuDung().toEpochDay() - today.toEpochDay();

            if (days < 0) {
                response.setTrangThaiHsd("Hết hạn!");
            } else if (days <= 7) {
                response.setTrangThaiHsd("Sắp hết hạn");
            } else {
                response.setTrangThaiHsd("Còn hạn");
            }
        }

        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public void importExcelLoNguyenLieu(MultipartFile file, Integer idNhanVien) {
        NhanVien nhanVien = null;

        if (idNhanVien != null) {
            nhanVien = nhanVienRepository.findById(idNhanVien)
                    .orElseThrow(() -> new RuntimeException(
                            "Không tìm thấy nhân viên thực hiện import"
                    ));
        }

        final NhanVien nhanVienImport = nhanVien;

        List<LoNguyenLieu> danhSachLo = ExcelHelper.readExcel(file, row -> {
            LoNguyenLieu lo = new LoNguyenLieu();

            lo.setMaLo(ExcelHelper.getStringValue(row.getCell(0)));

            Integer idNguyenLieu = ExcelHelper.getIntegerValue(row.getCell(1));
            NguyenLieu tempNguyenLieu = new NguyenLieu();
            tempNguyenLieu.setIdNguyenLieu(idNguyenLieu);
            lo.setNguyenLieu(tempNguyenLieu);

            String soLuongText = ExcelHelper.getStringValue(row.getCell(2));
            lo.setSoLuongTon(parseSoLuong(soLuongText));

            lo.setHanSuDung(ExcelHelper.getLocalDateValue(row.getCell(3)));
            lo.setNgayNhap(LocalDateTime.now());
            lo.setTrangThai(1);
            lo.setNhanVien(nhanVienImport);

            return lo;
        });

        for (LoNguyenLieu lo : danhSachLo) {
            if (lo.getMaLo() == null || lo.getMaLo().trim().isEmpty()) {
                continue;
            }

            String maLo = lo.getMaLo().trim();

            if (maLo.length() > 50) {
                throw new RuntimeException(
                        "Lỗi file Excel: Mã lô [" + maLo + "] vượt quá 50 ký tự"
                );
            }

            lo.setMaLo(maLo);

            if (lo.getNguyenLieu() == null
                    || lo.getNguyenLieu().getIdNguyenLieu() == null) {
                throw new RuntimeException(
                        "Lỗi file Excel: ID nguyên liệu không được để trống"
                );
            }

            Integer idNguyenLieu = lo.getNguyenLieu().getIdNguyenLieu();

            NguyenLieu nguyenLieu = nguyenLieuRepository.findById(idNguyenLieu)
                    .orElseThrow(() -> new RuntimeException(
                            "Lỗi file Excel: Không tìm thấy nguyên liệu có ID = "
                                    + idNguyenLieu
                    ));

            if (lo.getSoLuongTon() == null || lo.getSoLuongTon() <= 0) {
                throw new RuntimeException(
                        "Lỗi file Excel: Số lượng của mã lô ["
                                + maLo
                                + "] phải lớn hơn 0"
                );
            }

            if (lo.getHanSuDung() == null) {
                throw new RuntimeException(
                        "Lỗi file Excel: Hạn sử dụng của mã lô ["
                                + maLo
                                + "] không được để trống"
                );
            }

            if (lo.getHanSuDung().isBefore(LocalDate.now())) {
                throw new RuntimeException(
                        "Lỗi file Excel: Mã lô ["
                                + maLo
                                + "] đã hết hạn"
                );
            }

            Optional<LoNguyenLieu> existingLoOpt = repository.findByMaLo(maLo);

            if (existingLoOpt.isPresent()) {
                LoNguyenLieu existingLo = existingLoOpt.get();

                if (!existingLo.getNguyenLieu()
                        .getIdNguyenLieu()
                        .equals(idNguyenLieu)) {
                    throw new RuntimeException(
                            "Lỗi file Excel: Mã lô ["
                                    + maLo
                                    + "] đã tồn tại nhưng thuộc nguyên liệu khác"
                    );
                }

                continue;
            }

            lo.setNguyenLieu(nguyenLieu);
            repository.save(lo);
        }
    }

    private Double parseSoLuong(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return Double.parseDouble(value.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    "Số lượng trong file Excel không hợp lệ: " + value
            );
        }
    }
}