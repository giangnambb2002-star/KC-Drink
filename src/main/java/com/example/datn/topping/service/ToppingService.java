package com.example.datn.topping.service;

import com.example.datn.common.PageResponse;
import com.example.datn.topping.dto.ToppingRequest;
import com.example.datn.topping.dto.ToppingResponse;
import com.example.datn.topping.entity.Topping;
import com.example.datn.topping.repository.ToppingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ToppingService {

    private final ToppingRepository repository;

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

        return toResponse(repository.save(topping));
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

        return toResponse(repository.save(topping));
    }

    public ToppingResponse lock(Integer id) {
        Topping topping = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy topping"));
        topping.setTrangThai(0);
        return toResponse(repository.save(topping));
    }

    public ToppingResponse unlock(Integer id) {
        Topping topping = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy topping"));
        topping.setTrangThai(1);
        return toResponse(repository.save(topping));
    }

    private ToppingResponse toResponse(Topping topping) {
        ToppingResponse response = new ToppingResponse();
        response.setIdTopping(topping.getIdTopping());
        response.setTenTopping(topping.getTenTopping());
        response.setGiaTopping(topping.getGiaTopping());
        response.setTrangThai(topping.getTrangThai());

        // 👉 Đã sửa thành getTongTonKho / setTongTonKho
        response.setTongTonKho(topping.getTongTonKho());

        return response;
    }
}