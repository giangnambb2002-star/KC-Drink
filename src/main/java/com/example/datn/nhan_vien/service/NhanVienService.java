package com.example.datn.nhan_vien.service;

import com.example.datn.nhan_vien.dto.NhanVienRequest;
import com.example.datn.nhan_vien.dto.NhanVienUpdateRequest;
import com.example.datn.nhan_vien.entity.NhanVien;
import com.example.datn.common.PageResponse;
import com.example.datn.nhan_vien.dto.NhanVienResponse;
import com.example.datn.nhat_ky_he_thong.service.NhatKyHeThongService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.example.datn.nhan_vien.repository.NhanVienRepository;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import com.example.datn.tai_khoan.repository.TaiKhoanRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NhanVienService {

    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final NhatKyHeThongService nhatKyHeThongService;

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
        if (taiKhoanRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email tài khoản đã tồn tại");
        }

        // ==========================================
        // ĐÃ SỬA: Tự động sinh mật khẩu 8 ký tự
        // ==========================================
        String autoPassword =
                "Kc@" + UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 8);
        TaiKhoan taiKhoan = TaiKhoan.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(autoPassword)) // Mã hóa mật khẩu vừa sinh
                .email(request.getEmail())
                .role(request.getChucVu())      // ADMIN hoặc STAFF
                .trangThai(1)
                .build();

        taiKhoan = taiKhoanRepository.save(taiKhoan);

        NhanVien nhanVien = new NhanVien();
        nhanVien.setTenNhanVien(request.getTenNhanVien());
        nhanVien.setSdt(request.getSdt());
        nhanVien.setEmail(request.getEmail());
        nhanVien.setGioiTinh(request.getGioiTinh());
        nhanVien.setNgaySinh(request.getNgaySinh());
        nhanVien.setChucVu(
                request.getChucVu().equals("ADMIN")
                        ? "Quản lý"
                        : "Nhân viên"
        );
        nhanVien.setTrangThai(1);
        nhanVien.setTaiKhoan(taiKhoan);

        NhanVien savedNhanVien = nhanVienRepository.save(nhanVien);
        nhatKyHeThongService.ghiLogCurrentUser(
                "THÊM",
                "NHÂN VIÊN",
                savedNhanVien.getIdNhanVien(),
                "Thêm nhân viên " + savedNhanVien.getTenNhanVien()
        );
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.getEmail());
            message.setSubject("[KC Drink] Thông báo cấp tài khoản nhân viên mới");
            message.setText("Xin chào " + request.getTenNhanVien() + ",\n\n" +
                    "Bạn đã được Admin cấp tài khoản làm việc trên hệ thống KC Drink.\n" +
                    "Thông tin đăng nhập của bạn như sau:\n\n" +
                    "- Tên đăng nhập: " + request.getUsername() + "\n" +
                    "- Mật khẩu khởi tạo: " + autoPassword + "\n\n" +
                    "Vui lòng đăng nhập và thực hiện đổi mật khẩu ngay lập tức để bảo mật thông tin.\n" +
                    "Trân trọng!");
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Lỗi gửi mail cấp tài khoản: " + e.getMessage());
        }

        return toResponse(savedNhanVien);
    }


    // CẬP NHẬT (Đã sửa bảo mật: Chặn không cho tự sửa tài khoản của chính mình)
    public NhanVienResponse update(
            Integer id,
            NhanVienUpdateRequest request,
            TaiKhoan currentTaiKhoan
    ) {
        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        // >>> THE CHỐT: Nếu ID tài khoản của nhân viên này trùng với tài khoản đang đăng nhập
        if (nhanVien.getTaiKhoan() != null && nhanVien.getTaiKhoan().getIdTaiKhoan().equals(currentTaiKhoan.getIdTaiKhoan())) {
            throw new RuntimeException("Bạn không thể tự chỉnh sửa thông tin cá nhân tại đây! Vui lòng sử dụng trang Cá nhân.");
        }

        Optional<NhanVien> sdtExist = nhanVienRepository.findBySdt(request.getSdt());
        if (sdtExist.isPresent() && !sdtExist.get().getIdNhanVien().equals(id)) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }

        Optional<NhanVien> emailExist = nhanVienRepository.findByEmail(request.getEmail());
        if (emailExist.isPresent() && !emailExist.get().getIdNhanVien().equals(id)) {
            throw new RuntimeException("Email đã tồn tại");
        }

        nhanVien.setTenNhanVien(request.getTenNhanVien());
        nhanVien.setSdt(request.getSdt());
        nhanVien.setEmail(request.getEmail());
        nhanVien.setGioiTinh(request.getGioiTinh());
        nhanVien.setNgaySinh(request.getNgaySinh());
        nhanVien.setChucVu(
                request.getChucVu().equals("ADMIN")
                        ? "Quản lý"
                        : "Nhân viên"
        );

        TaiKhoan taiKhoan = nhanVien.getTaiKhoan();
        taiKhoan.setEmail(request.getEmail());
        taiKhoan.setRole(request.getChucVu());
        taiKhoanRepository.save(taiKhoan);

        NhanVien savedNhanVien = nhanVienRepository.save(nhanVien);
        nhatKyHeThongService.ghiLogCurrentUser(
                "CẬP NHẬT",
                "NHÂN VIÊN",
                savedNhanVien.getIdNhanVien(),
                "Cập nhật nhân viên " + savedNhanVien.getTenNhanVien()
        );
        return toResponse(savedNhanVien);
    }

    public NhanVienResponse lock(Integer id) {
        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        nhanVien.setTrangThai(0);
        TaiKhoan taiKhoan = nhanVien.getTaiKhoan();
        taiKhoan.setTrangThai(0);
        taiKhoanRepository.save(taiKhoan);

        NhanVien savedNhanVien = nhanVienRepository.save(nhanVien);
        nhatKyHeThongService.ghiLogCurrentUser(
                "KHÓA",
                "NHÂN VIÊN",
                savedNhanVien.getIdNhanVien(),
                "Khóa nhân viên " + savedNhanVien.getTenNhanVien()
        );
        return toResponse(savedNhanVien);
    }

    public NhanVienResponse unlock(Integer id) {
        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        nhanVien.setTrangThai(1);
        TaiKhoan taiKhoan = nhanVien.getTaiKhoan();
        taiKhoan.setTrangThai(1);
        taiKhoanRepository.save(taiKhoan);

        NhanVien savedNhanVien = nhanVienRepository.save(nhanVien);
        nhatKyHeThongService.ghiLogCurrentUser(
                "MỞ KHÓA",
                "NHÂN VIÊN",
                savedNhanVien.getIdNhanVien(),
                "Mở khóa nhân viên " + savedNhanVien.getTenNhanVien()
        );
        return toResponse(savedNhanVien);
    }

    private NhanVienResponse toResponse(NhanVien nhanVien) {
        NhanVienResponse response = new NhanVienResponse();
        response.setIdNhanVien(nhanVien.getIdNhanVien());
        response.setTenNhanVien(nhanVien.getTenNhanVien());
        response.setSdt(nhanVien.getSdt());
        response.setEmail(nhanVien.getEmail());
        response.setChucVu(nhanVien.getChucVu());
        response.setGioiTinh(nhanVien.getGioiTinh());
        response.setNgaySinh(nhanVien.getNgaySinh());
        response.setTrangThai(nhanVien.getTrangThai());
        if (nhanVien.getTaiKhoan() != null) {
            response.setIdTaiKhoan(nhanVien.getTaiKhoan().getIdTaiKhoan());
            response.setUsername(nhanVien.getTaiKhoan().getUsername());
        }
        return response;
    }
}