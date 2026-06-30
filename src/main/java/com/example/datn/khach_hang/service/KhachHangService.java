package com.example.datn.khach_hang.service;

import com.example.datn.common.PageResponse;
import com.example.datn.dia_chi.repository.DiaChiRepository;
import com.example.datn.khach_hang.dto.KhachHangRequest;
import com.example.datn.khach_hang.dto.KhachHangResponse;
import com.example.datn.khach_hang.entity.KhachHang;
import com.example.datn.khach_hang.repository.KhachHangRepository;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import com.example.datn.tai_khoan.repository.TaiKhoanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KhachHangService {

    private final KhachHangRepository repository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final DiaChiRepository diaChiRepository;


    public PageResponse<KhachHangResponse> getAll(
            String keyword,
            Integer trangThai, // Bổ sung tham số trạng thái
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // Gọi thẳng hàm Query tổng hợp (đảm bảo bạn đã thêm hàm này vào KhachHangRepository nhé)
        Page<KhachHang> pageData = repository.searchKhachHang(keyword, trangThai, pageable);

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

    public KhachHangResponse getById(Integer id) {

        KhachHang khachHang = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        return toResponse(khachHang);
    }
    public KhachHangResponse create(KhachHangRequest request) {

        Optional<KhachHang> existing = repository.findBySdt(request.getSdt());

        if (existing.isPresent()) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }
        TaiKhoan taiKhoan = null;
        if (request.getIdTaiKhoan() != null) {
            taiKhoan = taiKhoanRepository.findById(request.getIdTaiKhoan())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        }
        KhachHang khachHang = new KhachHang();

        khachHang.setTenKhachHang(request.getTenKhachHang());
        khachHang.setSdt(request.getSdt());
        khachHang.setEmail(request.getEmail());
        khachHang.setGioiTinh(request.getGioiTinh());
        khachHang.setNgaySinh(request.getNgaySinh());
        khachHang.setDiemTichLuy(0);
        khachHang.setTaiKhoan(taiKhoan);
        khachHang.setTrangThai(1);

        return toResponse(repository.save(khachHang));
    }
    public KhachHangResponse update(Integer id, KhachHangRequest request) {

        KhachHang khachHang = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        TaiKhoan taiKhoan = null;
        if (request.getIdTaiKhoan() != null) {
            taiKhoan = taiKhoanRepository.findById(request.getIdTaiKhoan())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        }
        Optional<KhachHang> existing = repository.findBySdt(request.getSdt());

        if (existing.isPresent() &&
                !existing.get().getIdKhachHang().equals(id)) {

            throw new RuntimeException("Số điện thoại đã tồn tại");
        }
        khachHang.setTenKhachHang(request.getTenKhachHang());
        khachHang.setSdt(request.getSdt());
        khachHang.setEmail(request.getEmail());
        khachHang.setGioiTinh(request.getGioiTinh());
        khachHang.setNgaySinh(request.getNgaySinh());
        khachHang.setTaiKhoan(taiKhoan);

        return toResponse(repository.save(khachHang));
    }
    public KhachHangResponse lock(Integer id) {
        KhachHang khachHang = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        khachHang.setTrangThai(0);
        if (khachHang.getIdKhachHang() == 7) {
            throw new RuntimeException("K" +
                    "không được khóa khách lẻ");
        }
        return toResponse(repository.save(khachHang));
    }

    public KhachHangResponse unlock(Integer id) {
        KhachHang khachHang = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        khachHang.setTrangThai(1);

        return toResponse(repository.save(khachHang));
    }
    private KhachHangResponse toResponse(KhachHang khachHang) {

        KhachHangResponse response = new KhachHangResponse();

        response.setIdKhachHang(khachHang.getIdKhachHang());
        response.setTenKhachHang(khachHang.getTenKhachHang());
        response.setSdt(khachHang.getSdt());
        response.setEmail(khachHang.getEmail());
        response.setGioiTinh(khachHang.getGioiTinh());
        response.setNgaySinh(khachHang.getNgaySinh());
        response.setTrangThai(khachHang.getTrangThai());
        response.setDiemTichLuy(khachHang.getDiemTichLuy());

        if (khachHang.getTaiKhoan() != null) {

            response.setIdTaiKhoan(
                    khachHang.getTaiKhoan().getIdTaiKhoan()
            );

            response.setUsername(
                    khachHang.getTaiKhoan().getUsername()
            );

        }
        diaChiRepository
                .findDefault(khachHang.getIdKhachHang())
                .ifPresent(item ->
                        response.setDiaChiMacDinh(
                                item.getDiaChi()
                        )
                );

        return response;
    }
    public KhachHangResponse findByPhone(String sdt) {
        KhachHang khachHang = repository.findBySdt(sdt)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        return toResponse(khachHang);
    }

}