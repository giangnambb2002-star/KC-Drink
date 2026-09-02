package com.example.datn.san_pham.service;

import com.example.datn.san_pham.dto.SizeRequest;
import com.example.datn.san_pham.dto.SizeResponse;
import com.example.datn.san_pham.entity.Size;
import com.example.datn.san_pham.repository.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SizeService {

    private final SizeRepository repository;

    public List<SizeResponse> getAll() {
        return repository.findAll().stream()
                .sorted((a, b) -> a.getThuTu().compareTo(b.getThuTu()))
                .map(this::toResponse)
                .toList();
    }

    public SizeResponse getById(Integer id) {
        return toResponse(findById(id));
    }

    public SizeResponse create(SizeRequest request) {
        String tenSize = request.getTenSize().trim();

        if (repository.existsByTenSizeIgnoreCase(tenSize)) {
            throw new RuntimeException("Tên size đã tồn tại");
        }

        Size size = new Size();
        size.setTenSize(tenSize);
        size.setPhuThu(request.getPhuThu());
        size.setThuTu(request.getThuTu());

        return toResponse(repository.save(size));
    }

    public SizeResponse update(Integer id, SizeRequest request) {
        Size size = findById(id);
        String tenSize = request.getTenSize().trim();

        if (!size.getTenSize().equalsIgnoreCase(tenSize)
                && repository.existsByTenSizeIgnoreCase(tenSize)) {
            throw new RuntimeException("Tên size đã tồn tại");
        }

        size.setTenSize(tenSize);
        size.setPhuThu(request.getPhuThu());
        size.setThuTu(request.getThuTu());

        return toResponse(repository.save(size));
    }

    public void delete(Integer id) {
        Size size = findById(id);
        repository.delete(size);
    }

    private Size findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy size"));
    }

    private SizeResponse toResponse(Size size) {
        SizeResponse response = new SizeResponse();
        response.setIdSize(size.getIdSize());
        response.setTenSize(size.getTenSize());
        response.setPhuThu(size.getPhuThu());
        response.setThuTu(size.getThuTu());
        return response;
    }
}