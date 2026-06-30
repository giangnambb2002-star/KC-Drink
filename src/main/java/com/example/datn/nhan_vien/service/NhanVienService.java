package com.example.datn.nhan_vien.service;

import com.example.datn.nhan_vien.dto.NhanVienRequest;
import com.example.datn.nhan_vien.dto.NhanVienUpdateRequest;
import com.example.datn.nhan_vien.entity.NhanVien;
import com.example.datn.common.PageResponse;
import com.example.datn.nhan_vien.dto.NhanVienResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.example.datn.nhan_vien.repository.NhanVienRepository;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import com.example.datn.tai_khoan.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NhanVienService {

    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;

    public PageResponse<NhanVienResponse> getAll(
            String keyword,
            String chucVu,
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
        Page<NhanVien> pageData = nhanVienRepository.search(
                keyword == null || keyword.isBlank() ? null : keyword,
                chucVu == null || chucVu.isBlank() ? null : chucVu,
                trangThai,
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
    public NhanVienResponse getById(Integer id) {
        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        return toResponse(nhanVien);
    }

    public NhanVienResponse create(NhanVienRequest request) {

        if (nhanVienRepository.findBySdt(request.getSdt()).isPresent()) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }
        if (nhanVienRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }
        if (taiKhoanRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("Mật khẩu không được để trống");
        }
        if (taiKhoanRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email tài khoản đã tồn tại");
        }

        TaiKhoan taiKhoan = TaiKhoan.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(request.getChucVu())      // ADMIN hoặc STAFF
                .trangThai(1)
                .build();

        taiKhoan = taiKhoanRepository.save(taiKhoan);

        NhanVien nhanVien = new NhanVien();

        nhanVien.setTenNhanVien(request.getTenNhanVien());
        nhanVien.setSdt(request.getSdt());
        nhanVien.setEmail(request.getEmail());

        nhanVien.setChucVu(
                request.getChucVu().equals("ADMIN")
                        ? "Quản lý"
                        : "Nhân viên"
        );

        nhanVien.setTrangThai(1);
        nhanVien.setTaiKhoan(taiKhoan);

        return toResponse(nhanVienRepository.save(nhanVien));
    }
    public NhanVienResponse update(
            Integer id,
            NhanVienUpdateRequest request
    ) {

        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy nhân viên"));

        Optional<NhanVien> sdtExist =
                nhanVienRepository.findBySdt(request.getSdt());

        if (sdtExist.isPresent()
                && !sdtExist.get().getIdNhanVien().equals(id)) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }

        Optional<NhanVien> emailExist =
                nhanVienRepository.findByEmail(request.getEmail());

        if (emailExist.isPresent()
                && !emailExist.get().getIdNhanVien().equals(id)) {
            throw new RuntimeException("Email đã tồn tại");
        }

        nhanVien.setTenNhanVien(request.getTenNhanVien());
        nhanVien.setSdt(request.getSdt());
        nhanVien.setEmail(request.getEmail());

        nhanVien.setChucVu(
                request.getChucVu().equals("ADMIN")
                        ? "Quản lý"
                        : "Nhân viên"
        );

        TaiKhoan taiKhoan = nhanVien.getTaiKhoan();
        taiKhoan.setEmail(request.getEmail());
        taiKhoan.setRole(request.getChucVu());

        taiKhoanRepository.save(taiKhoan);

        return toResponse(
                nhanVienRepository.save(nhanVien)
        );
    }
    public NhanVienResponse lock(Integer id) {

        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy nhân viên"));

        nhanVien.setTrangThai(0);

        TaiKhoan taiKhoan = nhanVien.getTaiKhoan();

        taiKhoan.setTrangThai(0);

        taiKhoanRepository.save(taiKhoan);

        return toResponse(
                nhanVienRepository.save(nhanVien)
        );

    }
    public NhanVienResponse unlock(Integer id) {

        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy nhân viên"));
        nhanVien.setTrangThai(1);
        TaiKhoan taiKhoan = nhanVien.getTaiKhoan();
        taiKhoan.setTrangThai(1);
        taiKhoanRepository.save(taiKhoan);
        return toResponse(
                nhanVienRepository.save(nhanVien)
        );

    }
    private NhanVienResponse toResponse(NhanVien nhanVien) {

        NhanVienResponse response = new NhanVienResponse();

        response.setIdNhanVien(nhanVien.getIdNhanVien());
        response.setTenNhanVien(nhanVien.getTenNhanVien());
        response.setSdt(nhanVien.getSdt());
        response.setEmail(nhanVien.getEmail());
        response.setChucVu(nhanVien.getChucVu());
        response.setTrangThai(nhanVien.getTrangThai());

        if (nhanVien.getTaiKhoan() != null) {
            response.setIdTaiKhoan(
                    nhanVien.getTaiKhoan().getIdTaiKhoan()
            );
            response.setUsername(
                    nhanVien.getTaiKhoan().getUsername()
            );
        }

        return response;
    }
}