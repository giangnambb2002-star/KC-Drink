package com.example.datn.topping.service;

import com.example.datn.common.PageResponse;
import com.example.datn.nhat_ky_he_thong.service.NhatKyHeThongService;
import com.example.datn.topping.dto.ToppingRequest;
import com.example.datn.topping.dto.ToppingResponse;
import com.example.datn.topping.entity.Topping;
import com.example.datn.topping.repository.LoToppingRepository;
import com.example.datn.topping.repository.ToppingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ToppingService {

    private final ToppingRepository repository;

    private final LoToppingRepository loToppingRepository;

    private final NhatKyHeThongService nhatKyHeThongService;

    public PageResponse<ToppingResponse> getAll(
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

        Page<Topping> pageData = repository.searchTopping(keyword, trangThai, pageable);

        return new PageResponse<>(
                pageData.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList(),
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.isLast()
        );
    }

    public ToppingResponse getById(Integer id) {
        Topping topping = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy topping"));
        return toResponse(topping);
    }

    public ToppingResponse create(ToppingRequest request) {
        if (repository.existsByTenToppingIgnoreCase(request.getTenTopping().trim())) {
            throw new RuntimeException("Tên topping đã tồn tại");
        }

        Topping topping = new Topping();
        topping.setTenTopping(request.getTenTopping().trim());
        topping.setGiaTopping(request.getGiaTopping());
        topping.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : 1);

        // 👉 Đã sửa thành getTongTonKho / setTongTonKho
        topping.setTongTonKho(request.getTongTonKho() != null ? request.getTongTonKho() : 0);

        Topping savedTopping = repository.save(topping);

        nhatKyHeThongService.ghiLogCurrentUser(
                "THÊM",
                "TOPPING",
                savedTopping.getIdTopping(),
                "Thêm topping " + savedTopping.getTenTopping()
        );

        return toResponse(savedTopping);
    }

    public ToppingResponse update(Integer id, ToppingRequest request) {
        Topping topping = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy topping"));

        if (!topping.getTenTopping().equalsIgnoreCase(request.getTenTopping().trim()) &&
                repository.existsByTenToppingIgnoreCase(request.getTenTopping().trim())) {
            throw new RuntimeException("Tên topping đã tồn tại");
        }

        topping.setTenTopping(request.getTenTopping().trim());
        topping.setGiaTopping(request.getGiaTopping());

        if(request.getTrangThai() != null){
            topping.setTrangThai(request.getTrangThai());
        }

        // 👉 Đã sửa thành getTongTonKho / setTongTonKho
        if (request.getTongTonKho() != null) {
            topping.setTongTonKho(request.getTongTonKho());
        }
        Topping savedTopping = repository.save(topping);

        nhatKyHeThongService.ghiLogCurrentUser(
                "CẬP NHẬT",
                "TOPPING",
                savedTopping.getIdTopping(),
                "Cập nhật topping " + savedTopping.getTenTopping()
        );

        return toResponse(savedTopping);
    }

    public ToppingResponse lock(Integer id) {
        Topping topping = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy topping"));
        topping.setTrangThai(0);
        Topping savedTopping = repository.save(topping);

        nhatKyHeThongService.ghiLogCurrentUser(
                "KHÓA",
                "TOPPING",
                savedTopping.getIdTopping(),
                "Khóa topping " + savedTopping.getTenTopping()
        );

        return toResponse(savedTopping);
    }

    public ToppingResponse unlock(Integer id) {
        Topping topping = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy topping"));
        topping.setTrangThai(1);
        Topping savedTopping = repository.save(topping);

        nhatKyHeThongService.ghiLogCurrentUser(
                "MỞ KHÓA",
                "TOPPING",
                savedTopping.getIdTopping(),
                "Mở khóa topping " + savedTopping.getTenTopping()
        );

        return toResponse(savedTopping);
    }

    private ToppingResponse toResponse(Topping topping) {
        ToppingResponse response = new ToppingResponse();
        response.setIdTopping(topping.getIdTopping());
        response.setTenTopping(topping.getTenTopping());
        response.setGiaTopping(topping.getGiaTopping());
        response.setTrangThai(topping.getTrangThai());
        BigDecimal tongTonKhoConHan = loToppingRepository.getTongTonKhoConHan(topping.getIdTopping());
        response.setTongTonKho(tongTonKhoConHan != null ? tongTonKhoConHan.intValue() : 0);


        return response;
    }
}