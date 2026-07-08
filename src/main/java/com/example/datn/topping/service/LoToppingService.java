package com.example.datn.topping.service;

import com.example.datn.common.PageResponse;
import com.example.datn.topping.dto.LoToppingRequest;
import com.example.datn.topping.dto.LoToppingResponse;
import com.example.datn.topping.entity.LoTopping;
import com.example.datn.topping.entity.Topping;
import com.example.datn.topping.repository.LoToppingRepository;
import com.example.datn.topping.repository.ToppingRepository;
import com.example.datn.nhan_vien.entity.NhanVien;
import com.example.datn.nhan_vien.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoToppingService {

    private final LoToppingRepository repository;
    private final ToppingRepository toppingRepository;
    private final NhanVienRepository nhanVienRepository;

    public PageResponse<LoToppingResponse> getAll(
            Integer idTopping, Integer trangThai, int page, int size, String sortBy, String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<LoTopping> pageData = repository.searchLoTopping(idTopping, trangThai, pageable);

        return new PageResponse<>(
                pageData.getContent().stream().map(this::toResponse).toList(),
                pageData.getNumber(), pageData.getSize(), pageData.getTotalElements(),
                pageData.getTotalPages(), pageData.isLast()
        );
    }

    @Transactional // Đảm bảo đồng bộ: Lỗi 1 cái là rollback cả 2 bảng
    public LoToppingResponse create(LoToppingRequest request) {
        // 1. Check Topping & Nhân viên
        Topping topping = toppingRepository.findById(request.getIdTopping())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Topping gốc"));

        NhanVien nhanVien = nhanVienRepository.findById(request.getIdNhanVien())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin nhân viên nhập kho"));

        // 2. Tạo lô mới
        LoTopping lo = new LoTopping();
        lo.setTopping(topping);
        lo.setMaLo(request.getMaLo());
        lo.setNhanVien(nhanVien);
        lo.setSoLuongNhap(request.getSoLuongNhap());
        lo.setSoLuongTon(request.getSoLuongNhap()); // Mới nhập thì Tồn = Nhập
        lo.setHanSuDung(request.getHanSuDung());
        lo.setNgayNhap(LocalDateTime.now());
        lo.setTrangThai(1);

        LoTopping savedLo = repository.save(lo);

        // 3. LOGIC QUAN TRỌNG: Cộng dồn vào bảng Topping gốc
        int currentTonKho = topping.getTongTonKho() != null ? topping.getTongTonKho() : 0;
        topping.setTongTonKho(currentTonKho + request.getSoLuongNhap());
        toppingRepository.save(topping);

        return toResponse(savedLo);
    }

    public LoToppingResponse updateStatus(Integer id, Integer trangThai) {
        LoTopping lo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lô Topping"));
        lo.setTrangThai(trangThai);
        return toResponse(repository.save(lo));
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
}