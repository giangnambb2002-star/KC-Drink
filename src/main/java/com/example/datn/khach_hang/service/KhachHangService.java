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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KhachHangService {

    private final KhachHangRepository repository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final DiaChiRepository diaChiRepository;

    // THÊM: Inject các công cụ mã hóa và gửi mail
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public PageResponse<KhachHangResponse> getAll(
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

        // LUỒNG MỚI: Tự động tạo tài khoản nếu không truyền lên idTaiKhoan
        if (request.getIdTaiKhoan() != null) {
            taiKhoan = taiKhoanRepository.findById(request.getIdTaiKhoan())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        } else {
            // 1. Kiểm tra xem SĐT này đã có trong bảng TaiKhoan chưa
            if (taiKhoanRepository.existsByUsername(request.getSdt())) {
                throw new RuntimeException("Số điện thoại này đã được đăng ký tài khoản trên hệ thống");
            }

            // 2. Kiểm tra email (nếu có nhập) đã tồn tại chưa
            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                if (taiKhoanRepository.existsByEmail(request.getEmail())) {
                    throw new RuntimeException("Email này đã được sử dụng cho một tài khoản khác");
                }
            }

            // 3. Tạo tài khoản mới
            taiKhoan = new TaiKhoan();
            taiKhoan.setUsername(request.getSdt()); // Lấy SĐT làm Tên đăng nhập
            taiKhoan.setEmail(request.getEmail());
            taiKhoan.setRole("USER");
            taiKhoan.setTrangThai(1);

            // Sinh mật khẩu ngẫu nhiên (8 ký tự)
            String rawPassword = UUID.randomUUID().toString().substring(0, 8);
            taiKhoan.setPassword(passwordEncoder.encode(rawPassword));

            // Lưu tài khoản vào DB
            taiKhoan = taiKhoanRepository.save(taiKhoan);

            // 4. Gửi email mật khẩu cho khách (nếu khách có cung cấp email)
            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                sendWelcomeEmail(request.getEmail(), request.getSdt(), rawPassword);
            }
        }

        // Tạo hồ sơ khách hàng và gắn với tài khoản
        KhachHang khachHang = new KhachHang();
        khachHang.setTenKhachHang(request.getTenKhachHang());
        khachHang.setSdt(request.getSdt());
        khachHang.setEmail(request.getEmail());
        khachHang.setGioiTinh(request.getGioiTinh());
        khachHang.setNgaySinh(request.getNgaySinh());
        khachHang.setDiemTichLuy(0);
        khachHang.setTaiKhoan(taiKhoan); // Gắn tài khoản vừa tạo
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

        if (existing.isPresent() && !existing.get().getIdKhachHang().equals(id)) {
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
            throw new RuntimeException("Không được khóa khách lẻ");
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
            response.setIdTaiKhoan(khachHang.getTaiKhoan().getIdTaiKhoan());
            response.setUsername(khachHang.getTaiKhoan().getUsername());
        }
        diaChiRepository
                .findDefault(khachHang.getIdKhachHang())
                .ifPresent(item -> response.setDiaChiMacDinh(item.getDiaChi()));

        return response;
    }

    public KhachHangResponse findByPhone(String sdt) {
        KhachHang khachHang = repository.findBySdt(sdt)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        return toResponse(khachHang);
    }

    // THÊM: Hàm phụ trợ để gửi email thông báo tài khoản
    private void sendWelcomeEmail(String email, String username, String password) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[KC Drink] Thông tin tài khoản khách hàng");
            message.setText("Xin chào,\n\n" +
                    "Cảm ơn bạn đã trở thành khách hàng của KC Drink.\n" +
                    "Hệ thống đã tự động tạo cho bạn một tài khoản mua hàng online với thông tin như sau:\n\n" +
                    "- Tên đăng nhập (hoặc Số điện thoại): " + username + "\n" +
                    "- Mật khẩu: " + password + "\n\n" +
                    "Bạn có thể sử dụng thông tin này để đăng nhập vào hệ thống của chúng tôi.\n" +
                    "Vui lòng đổi mật khẩu sau lần đăng nhập đầu tiên để bảo mật tài khoản.\n\n" +
                    "Trân trọng,\nKC Drink Team");
            mailSender.send(message);
        } catch (Exception e) {
            // Dùng try-catch để nếu gửi mail lỗi (VD: sai email) thì hệ thống vẫn lưu được khách hàng
            System.err.println("Không thể gửi email thông báo tài khoản: " + e.getMessage());
        }
    }
}