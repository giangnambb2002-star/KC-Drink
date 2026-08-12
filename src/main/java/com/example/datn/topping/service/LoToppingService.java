package com.example.datn.topping.service;

import com.example.datn.common.ExcelHelper;
import com.example.datn.common.PageResponse;
import com.example.datn.nhan_vien.entity.NhanVien;
import com.example.datn.nhan_vien.repository.NhanVienRepository;
import com.example.datn.nhat_ky_he_thong.service.NhatKyHeThongService;
import com.example.datn.topping.dto.LoToppingRequest;
import com.example.datn.topping.dto.LoToppingResponse;
import com.example.datn.topping.entity.LoTopping;
import com.example.datn.topping.entity.Topping;
import com.example.datn.topping.repository.LoToppingRepository;
import com.example.datn.topping.repository.ToppingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoToppingService {

    private final LoToppingRepository repository;
    private final ToppingRepository toppingRepository;
    private final NhanVienRepository nhanVienRepository;
    private final NhatKyHeThongService nhatKyHeThongService;

    public PageResponse<LoToppingResponse> getAll(Integer idTopping, Integer trangThai, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<LoTopping> pageData = repository.searchLoTopping(idTopping, trangThai, pageable);

        return new PageResponse<>(
                pageData.getContent().stream().map(this::toResponse).toList(),
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.isLast()
        );
    }

    @Transactional
    public LoToppingResponse create(LoToppingRequest request) {
        Topping topping = toppingRepository.findById(request.getIdTopping())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Topping gốc"));

        NhanVien nhanVien = nhanVienRepository.findById(request.getIdNhanVien())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin nhân viên nhập kho"));

        LoTopping lo = new LoTopping();
        lo.setTopping(topping);
        lo.setMaLo(request.getMaLo());
        lo.setNhanVien(nhanVien);
        lo.setSoLuongNhap(request.getSoLuongNhap());
        lo.setSoLuongTon(request.getSoLuongNhap());
        lo.setHanSuDung(request.getHanSuDung());
        lo.setNgayNhap(LocalDateTime.now());
        lo.setTrangThai(1);

        LoTopping savedLo = repository.save(lo);

        int currentTonKho = topping.getTongTonKho() != null ? topping.getTongTonKho() : 0;
        topping.setTongTonKho(currentTonKho + request.getSoLuongNhap());
        toppingRepository.save(topping);

        nhatKyHeThongService.ghiLogCurrentUser(
                "NHẬP LÔ",
                "LÔ TOPPING",
                savedLo.getIdLoTopping(),
                "Nhập lô " + savedLo.getMaLo() + " cho topping " + topping.getTenTopping() + ", số lượng " + savedLo.getSoLuongNhap()
        );

        return toResponse(savedLo);
    }

    @Transactional
    public LoToppingResponse lock(Integer id) {
        LoTopping lo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lô Topping với ID: " + id));

        if (lo.getTrangThai() != null && lo.getTrangThai() == 0) {
            return toResponse(lo);
        }

        lo.setTrangThai(0);
        LoTopping savedLo = repository.save(lo);

        Topping topping = lo.getTopping();
        if (topping != null) {
            int currentTon = topping.getTongTonKho() != null ? topping.getTongTonKho() : 0;
            int loTon = lo.getSoLuongTon() != null ? lo.getSoLuongTon() : 0;
            topping.setTongTonKho(Math.max(0, currentTon - loTon));
            toppingRepository.save(topping);
        }

        nhatKyHeThongService.ghiLogCurrentUser(
                "KHÓA",
                "LÔ TOPPING",
                savedLo.getIdLoTopping(),
                "Khóa lô topping " + savedLo.getMaLo()
        );

        return toResponse(savedLo);
    }

    @Transactional
    public LoToppingResponse unlock(Integer id) {
        LoTopping lo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lô Topping với ID: " + id));

        if (lo.getTrangThai() != null && lo.getTrangThai() == 1) {
            return toResponse(lo);
        }

        lo.setTrangThai(1);
        LoTopping savedLo = repository.save(lo);

        Topping topping = lo.getTopping();
        if (topping != null) {
            int currentTon = topping.getTongTonKho() != null ? topping.getTongTonKho() : 0;
            int loTon = lo.getSoLuongTon() != null ? lo.getSoLuongTon() : 0;
            topping.setTongTonKho(currentTon + loTon);
            toppingRepository.save(topping);
        }

        nhatKyHeThongService.ghiLogCurrentUser(
                "MỞ KHÓA",
                "LÔ TOPPING",
                savedLo.getIdLoTopping(),
                "Mở khóa lô topping " + savedLo.getMaLo()
        );

        return toResponse(savedLo);
    }

    private LoToppingResponse toResponse(LoTopping lo) {
        LoToppingResponse response = new LoToppingResponse();
        response.setIdLoTopping(lo.getIdLoTopping());
        response.setMaLo(lo.getMaLo());
        response.setIdTopping(lo.getTopping().getIdTopping());
        response.setTenTopping(lo.getTopping().getTenTopping());
        response.setSoLuongNhap(lo.getSoLuongNhap());
        response.setSoLuongTon(lo.getSoLuongTon());
        response.setHanSuDung(lo.getHanSuDung());
        response.setNgayNhap(lo.getNgayNhap());
        response.setTrangThai(lo.getTrangThai());

        if (lo.getNhanVien() != null) {
            response.setTenNhanVien(lo.getNhanVien().getTenNhanVien());
        }

        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public void importExcelLoTopping(MultipartFile file, Integer idNhanVien) {
        NhanVien nhanVien = null;
        if (idNhanVien != null) {
            nhanVien = nhanVienRepository.findById(idNhanVien)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin nhân viên thực hiện import!"));
        }

        final NhanVien nvImport = nhanVien;

        List<LoTopping> dtoList = ExcelHelper.readExcel(file, row -> {
            LoTopping lo = new LoTopping();

            String maLo = ExcelHelper.getStringValue(row.getCell(0));
            lo.setMaLo(maLo != null ? maLo.trim().toUpperCase() : null);

            Integer idTopping = ExcelHelper.getIntegerValue(row.getCell(1));
            Topping tempTopping = new Topping();
            tempTopping.setIdTopping(idTopping);
            lo.setTopping(tempTopping);

            Integer soLuong = ExcelHelper.getIntegerValue(row.getCell(2));
            lo.setSoLuongNhap(soLuong);
            lo.setSoLuongTon(soLuong);
            lo.setHanSuDung(ExcelHelper.getLocalDateValue(row.getCell(3)));
            lo.setNgayNhap(LocalDateTime.now());
            lo.setTrangThai(1);
            lo.setNhanVien(nvImport);

            return lo;
        });

        int soLuongThemMoi = 0;
        int soLuongBoQua = 0;

        for (LoTopping lo : dtoList) {
            if (lo.getMaLo() == null || lo.getMaLo().isEmpty()) {
                soLuongBoQua++;
                continue;
            }

            Integer idTopping = lo.getTopping().getIdTopping();
            Topping topping = toppingRepository.findById(idTopping)
                    .orElseThrow(() -> new RuntimeException("Lỗi file Excel: Không tìm thấy Topping gốc có ID = " + idTopping));

            Optional<LoTopping> existingLoOpt = repository.findByMaLoIgnoreCase(lo.getMaLo());

            if (existingLoOpt.isPresent()) {
                LoTopping existingLo = existingLoOpt.get();

                if (!existingLo.getTopping().getIdTopping().equals(idTopping)) {
                    throw new RuntimeException("Lỗi file Excel: Mã lô [" + lo.getMaLo() + "] đã tồn tại nhưng thuộc về Topping khác!");
                }

                soLuongBoQua++;
                continue;
            }

            lo.setTopping(topping);
            repository.save(lo);
            soLuongThemMoi++;

            int currentTonKho = topping.getTongTonKho() != null ? topping.getTongTonKho() : 0;
            int importSoLuong = lo.getSoLuongNhap() != null ? lo.getSoLuongNhap() : 0;
            topping.setTongTonKho(currentTonKho + importSoLuong);
            toppingRepository.save(topping);
        }

        if (soLuongThemMoi > 0) {
            nhatKyHeThongService.ghiLogCurrentUser(
                    "IMPORT EXCEL",
                    "LÔ TOPPING",
                    null,
                    "Import Excel lô topping: thêm mới " + soLuongThemMoi + " lô, bỏ qua " + soLuongBoQua + " dòng"
            );
        }
    }}