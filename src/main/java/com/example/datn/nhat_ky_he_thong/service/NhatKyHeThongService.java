package com.example.datn.nhat_ky_he_thong.service;

import com.example.datn.common.PageResponse;
import com.example.datn.nhat_ky_he_thong.dto.NhatKyHeThongResponse;
import com.example.datn.nhat_ky_he_thong.entity.NhatKyHeThong;
import com.example.datn.nhat_ky_he_thong.repository.NhatKyHeThongRepository;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NhatKyHeThongService {

    private final NhatKyHeThongRepository repository;

    public void ghiLog(
            TaiKhoan taiKhoan,
            String hanhDong,
            String doiTuong,
            Integer idDoiTuong,
            String noiDung
    ) {
        NhatKyHeThong nhatKy = new NhatKyHeThong();
        nhatKy.setIdTaiKhoan(taiKhoan.getIdTaiKhoan());
        nhatKy.setUsername(taiKhoan.getUsername());
        nhatKy.setRole(taiKhoan.getRole());
        nhatKy.setHanhDong(hanhDong);
        nhatKy.setDoiTuong(doiTuong);
        nhatKy.setIdDoiTuong(idDoiTuong);
        nhatKy.setNoiDung(noiDung);
        nhatKy.setThoiGian(LocalDateTime.now());
        repository.save(nhatKy);
    }

    public PageResponse<NhatKyHeThongResponse> getAll(
            String username,
            String hanhDong,
            String doiTuong,
            LocalDateTime tuNgay,
            LocalDateTime denNgay,
            Pageable pageable
    ) {
        Page<NhatKyHeThong> pageData = repository.search(
                username == null || username.isBlank() ? null : username.trim(),
                hanhDong == null || hanhDong.isBlank() ? null : hanhDong,
                doiTuong == null || doiTuong.isBlank() ? null : doiTuong,
                tuNgay,
                denNgay,
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
    private NhatKyHeThongResponse toResponse(NhatKyHeThong nhatKy) {
        NhatKyHeThongResponse response = new NhatKyHeThongResponse();
        response.setIdNhatKy(nhatKy.getIdNhatKy());
        response.setIdTaiKhoan(nhatKy.getIdTaiKhoan());
        response.setUsername(nhatKy.getUsername());
        response.setRole(nhatKy.getRole());
        response.setHanhDong(nhatKy.getHanhDong());
        response.setDoiTuong(nhatKy.getDoiTuong());
        response.setIdDoiTuong(nhatKy.getIdDoiTuong());
        response.setNoiDung(nhatKy.getNoiDung());
        response.setThoiGian(nhatKy.getThoiGian());
        return response;
    }

    public void ghiLogCurrentUser(
            String hanhDong,
            String doiTuong,
            Integer idDoiTuong,
            String noiDung
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof TaiKhoan)) {
            return;
        }
        TaiKhoan taiKhoan = (TaiKhoan) authentication.getPrincipal();
        ghiLog(taiKhoan, hanhDong, doiTuong, idDoiTuong, noiDung);
    }
    public void ghiLogHeThong(
            String hanhDong,
            String doiTuong,
            Integer idDoiTuong,
            String noiDung
    ) {
        NhatKyHeThong nhatKy = new NhatKyHeThong();
        nhatKy.setIdTaiKhoan(null);
        nhatKy.setUsername("SYSTEM");
        nhatKy.setRole("SYSTEM");
        nhatKy.setHanhDong(hanhDong);
        nhatKy.setDoiTuong(doiTuong);
        nhatKy.setIdDoiTuong(idDoiTuong);
        nhatKy.setNoiDung(noiDung);
        nhatKy.setThoiGian(LocalDateTime.now());

        repository.save(nhatKy);
    }
}