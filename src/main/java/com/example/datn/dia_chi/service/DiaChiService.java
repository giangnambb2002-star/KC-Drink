package com.example.datn.dia_chi.service;

import com.example.datn.dia_chi.dto.DiaChiRequest;
import com.example.datn.dia_chi.dto.DiaChiResponse;
import com.example.datn.dia_chi.entity.DiaChiKhachHang;
import com.example.datn.dia_chi.repository.DiaChiRepository;
import com.example.datn.khach_hang.entity.KhachHang;
import com.example.datn.khach_hang.repository.KhachHangRepository;
import com.example.datn.common.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaChiService {

    private final DiaChiRepository diaChiRepository;
    private final KhachHangRepository khachHangRepository;

    public PageResponse<DiaChiResponse> getAll(
            Integer idKhachHang,
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DiaChiKhachHang> pageData =
                diaChiRepository.findByKhachHang_IdKhachHang(
                        idKhachHang,
                        pageable
                );

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
    public DiaChiResponse getById(Integer id) {

        DiaChiKhachHang diaChi =
                diaChiRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Không tìm thấy địa chỉ"));

        return toResponse(diaChi);

    }

    public DiaChiResponse create(DiaChiRequest request) {

        KhachHang khachHang =
                khachHangRepository.findById(request.getIdKhachHang())
                        .orElseThrow(() ->
                                new RuntimeException("Không tìm thấy khách hàng"));

        if (Boolean.TRUE.equals(request.getMacDinh())) {
            List<DiaChiKhachHang> ds =
                    diaChiRepository.findAllByKhachHang_IdKhachHang(
                            request.getIdKhachHang()
                    );
            for (DiaChiKhachHang item : ds) {

                item.setMacDinh(false);

                diaChiRepository.save(item);

            }

        }

        DiaChiKhachHang diaChi = new DiaChiKhachHang();
        diaChi.setKhachHang(khachHang);
        diaChi.setTenNguoiNhan(
                request.getTenNguoiNhan()
        );
        diaChi.setSdtNguoiNhan(
                request.getSdtNguoiNhan()
        );
        diaChi.setDiaChi(
                request.getDiaChi()
        );
        diaChi.setMacDinh(
                request.getMacDinh()
        );
        diaChi.setTrangThai(1);
        return toResponse(
                diaChiRepository.save(diaChi)
        );
    }
    public DiaChiResponse update(
            Integer id,
            DiaChiRequest request
    ) {

        DiaChiKhachHang diaChi =
                diaChiRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Không tìm thấy địa chỉ"));

        KhachHang khachHang =
                khachHangRepository.findById(request.getIdKhachHang())
                        .orElseThrow(() ->
                                new RuntimeException("Không tìm thấy khách hàng"));

        if (Boolean.TRUE.equals(request.getMacDinh())) {
            List<DiaChiKhachHang> ds =
                    diaChiRepository.findAllByKhachHang_IdKhachHang(
                            request.getIdKhachHang()
                    );
            for (DiaChiKhachHang item : ds) {

                if (!item.getIdDiaChi().equals(id)) {

                    item.setMacDinh(false);

                    diaChiRepository.save(item);

                }
            }
        }
        diaChi.setKhachHang(khachHang);
        diaChi.setTenNguoiNhan(
                request.getTenNguoiNhan()
        );
        diaChi.setSdtNguoiNhan(
                request.getSdtNguoiNhan()
        );
        diaChi.setDiaChi(
                request.getDiaChi()
        );

        diaChi.setMacDinh(
                request.getMacDinh()
        );

        return toResponse(
                diaChiRepository.save(diaChi)
        );

    }

    public DiaChiResponse lock(Integer id) {

        DiaChiKhachHang diaChi =
                diaChiRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Không tìm thấy địa chỉ"));

        if (Boolean.TRUE.equals(diaChi.getMacDinh())) {
            throw new RuntimeException("Không được khóa địa chỉ mặc định");
        }

        diaChi.setTrangThai(0);

        return toResponse(
                diaChiRepository.save(diaChi)
        );

    }

    public DiaChiResponse unlock(Integer id) {
        DiaChiKhachHang diaChi =
                diaChiRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Không tìm thấy địa chỉ"));
        diaChi.setTrangThai(1);
        return toResponse(
                diaChiRepository.save(diaChi)
        );
    }
    public DiaChiResponse setDefault(Integer id) {

        DiaChiKhachHang diaChi =
                diaChiRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Không tìm thấy địa chỉ"));

        List<DiaChiKhachHang> ds =
                diaChiRepository.findAllByKhachHang_IdKhachHang(
                        diaChi.getKhachHang().getIdKhachHang()
                );

        for (DiaChiKhachHang item : ds) {
            item.setMacDinh(false);
            diaChiRepository.save(item);

        }
        diaChi.setMacDinh(true);
        return toResponse(
                diaChiRepository.save(diaChi)
        );

    }
    private DiaChiResponse toResponse(
            DiaChiKhachHang diaChi
    ) {

        DiaChiResponse response =
                new DiaChiResponse();

        response.setIdDiaChi(
                diaChi.getIdDiaChi()
        );

        response.setIdKhachHang(
                diaChi.getKhachHang().getIdKhachHang()
        );

        response.setTenNguoiNhan(
                diaChi.getTenNguoiNhan()
        );

        response.setSdtNguoiNhan(
                diaChi.getSdtNguoiNhan()
        );

        response.setDiaChi(
                diaChi.getDiaChi()
        );

        response.setMacDinh(
                diaChi.getMacDinh()
        );

        response.setTrangThai(
                diaChi.getTrangThai()
        );

        return response;

    }

}