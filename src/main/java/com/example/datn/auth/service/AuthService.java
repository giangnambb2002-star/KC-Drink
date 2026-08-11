package com.example.datn.auth.service;

import com.example.datn.auth.dto.ChangePhoneRequest;
import com.example.datn.auth.dto.CustomerProfileUpdateRequest;
import com.example.datn.auth.dto.LoginRequest;
import com.example.datn.auth.dto.MeResponse;
import com.example.datn.auth.dto.RegisterRequest;
import com.example.datn.auth.dto.ChangePasswordRequest;
import com.example.datn.khach_hang.entity.KhachHang; // Thêm import Entity KhachHang
import com.example.datn.khach_hang.repository.KhachHangRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import java.util.UUID;
import com.example.datn.auth.dto.ProfileUpdateRequest;
import com.example.datn.nhan_vien.entity.NhanVien;
import org.springframework.transaction.annotation.Transactional;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import com.example.datn.tai_khoan.repository.TaiKhoanRepository;
import com.example.datn.nhan_vien.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class AuthService {

    private final TaiKhoanRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final NhanVienRepository nhanVienRepository;
    private final KhachHangRepository khachHangRepository;
    private final JavaMailSender mailSender;

    public void register(RegisterRequest request) {

        if (repository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username hoặc sdt đã tồn tại");
        }
        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }
        // 1. TẠO VÀ LƯU TÀI KHOẢN
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setUsername(request.getUsername());
        taiKhoan.setEmail(request.getEmail());
        taiKhoan.setPassword(passwordEncoder.encode(request.getPassword()));
        taiKhoan.setRole("USER");
        taiKhoan.setTrangThai(1);

        TaiKhoan savedTaiKhoan = repository.save(taiKhoan);

        // 2. TẠO VÀ LƯU HỒ SƠ KHÁCH HÀNG (Map đúng theo file KhachHang.java)
        KhachHang khachHang = new KhachHang();
        khachHang.setTenKhachHang("Khách hàng " + request.getUsername());
        khachHang.setSdt(request.getUsername());
        khachHang.setEmail(request.getEmail());
        khachHang.setDiemTichLuy(0);
        khachHang.setTrangThai(1);
        khachHang.setTaiKhoan(savedTaiKhoan);

        khachHangRepository.save(khachHang);

        // 3. GỬI MAIL CHÀO MỪNG
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.getEmail());
            message.setSubject("[KC Drink] Đăng ký tài khoản thành công!");
            message.setText("Xin chào,\n\n" +
                    "Chào mừng bạn đến với hệ thống đặt hàng online của KC Drink.\n" +
                    "Tài khoản của bạn đã được tạo thành công với thông tin sau:\n\n" +
                    "- Tên đăng nhập / Số điện thoại: " + request.getUsername() + "\n" +
                    "- Email: " + request.getEmail() + "\n\n" +
                    "Bây giờ bạn có thể đăng nhập vào website để đặt món và bắt đầu tích lũy điểm thưởng nhé!\n\n" +
                    "Trân trọng,\nĐội ngũ KC Drink.");
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Lỗi gửi mail chào mừng: " + e.getMessage());
        }
    }

    public String login(LoginRequest request) {

        TaiKhoan taiKhoan = repository.findByUsername(
                request.getUsernameOrEmail()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Tài khoản không tồn tại"));
        if (taiKhoan.getTrangThai() == 0) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }
        if (!passwordEncoder.matches(request.getPassword(), taiKhoan.getPassword()
        )) {
            throw new RuntimeException(
                    "Sai mật khẩu");
        }
        return jwtService.generateToken(
                taiKhoan.getUsername()
        );
    }
    public MeResponse getCurrentUser(TaiKhoan taiKhoan) {
        String tenNguoiDung = null;
        String chucVu = null;
        String sdt = null;
        Integer diemTichLuy = null;
        Integer idNhanVien = null; // BƯỚC 1: Khai báo thêm biến để hứng ID nhân viên
        Boolean gioiTinh = null;
        LocalDate ngaySinh = null;

        if ("STAFF".equals(taiKhoan.getRole()) || "ADMIN".equals(taiKhoan.getRole())) {
            var nhanVien = nhanVienRepository.findByTaiKhoan(taiKhoan);
            if (nhanVien.isPresent()) {
                tenNguoiDung = nhanVien.get().getTenNhanVien();
                chucVu = nhanVien.get().getChucVu();
                idNhanVien = nhanVien.get().getIdNhanVien(); // BƯỚC 2: Lấy ID từ database ra
                gioiTinh = nhanVien.get().getGioiTinh();
                ngaySinh = nhanVien.get().getNgaySinh();
                sdt = nhanVien.get().getSdt();

            }
        }
        if ("USER".equals(taiKhoan.getRole())) {
            var khachHang = khachHangRepository.findByTaiKhoan(taiKhoan);
            if (khachHang.isPresent()) {
                tenNguoiDung = khachHang.get().getTenKhachHang();
                diemTichLuy = khachHang.get().getDiemTichLuy();
                sdt = khachHang.get().getSdt();
                gioiTinh = khachHang.get().getGioiTinh();
                ngaySinh = khachHang.get().getNgaySinh();
                
            }
        }
        // BƯỚC 3: Dùng Setter thay cho Constructor để tránh lỗi thiếu/sai thứ tự tham số
        MeResponse response = new MeResponse();
        response.setIdTaiKhoan(taiKhoan.getIdTaiKhoan());
        response.setUsername(taiKhoan.getUsername());
        response.setEmail(taiKhoan.getEmail());
        response.setRole(taiKhoan.getRole());
        response.setTenNguoiDung(tenNguoiDung);
        response.setChucVu(chucVu);
        response.setSdt(sdt);
        response.setDiemTichLuy(diemTichLuy);
        response.setIdNhanVien(idNhanVien); // Nhét thẻ nhân viên vào đây để gửi về Vue!
        response.setGioiTinh(gioiTinh);
        response.setNgaySinh(ngaySinh);
        if (("ADMIN".equals(taiKhoan.getRole()) || "STAFF".equals(taiKhoan.getRole()))
                && taiKhoan.getNgayTao() != null) {
            long soNgayDongHanh = LocalDate.now().toEpochDay()
                    - taiKhoan.getNgayTao().toLocalDate().toEpochDay() + 1;
            response.setSoNgayDongHanh(soNgayDongHanh);
        }
        return response;
    }

    public void changePassword(
            TaiKhoan taiKhoan,
            ChangePasswordRequest request
    ) {
        if (!passwordEncoder.matches(request.getOldPassword(), taiKhoan.getPassword()
        )) { throw new RuntimeException("Mật khẩu cũ không chính xác");
        }if (request.getOldPassword()
                .equals(request.getNewPassword())) {
            throw new RuntimeException(
                    "Mật khẩu mới phải khác mật khẩu cũ"
            );
        }
        taiKhoan.setPassword(passwordEncoder.encode(request.getNewPassword())
        );
        repository.save(taiKhoan);
    }

    public void forgotPassword(String email) {
        TaiKhoan taiKhoan = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống"));

        String newPassword = UUID.randomUUID().toString().substring(0, 8);
        taiKhoan.setPassword(passwordEncoder.encode(newPassword));
        repository.save(taiKhoan);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[Hệ Thống] Cấp lại mật khẩu mới");
        message.setText("Xin chào,\n\n" +
                "Hệ thống đã nhận được yêu cầu cấp lại mật khẩu của bạn.\n" +
                "Tên đăng nhập / Số điện thoại của bạn: " + taiKhoan.getUsername() + "\n" +
                "Mật khẩu mới của bạn là: " + newPassword + "\n\n" +
                "Vui lòng đăng nhập và đổi lại mật khẩu ngay lập tức để bảo mật tài khoản.\n" +
                "Trân trọng!");
        mailSender.send(message);
    }
    @Transactional
    public void updateProfile(TaiKhoan taiKhoan, ProfileUpdateRequest request) {
        if (!"ADMIN".equals(taiKhoan.getRole()) && !"STAFF".equals(taiKhoan.getRole())) {
            throw new RuntimeException("Chức năng này chỉ áp dụng cho nhân viên");
        }

        NhanVien nhanVien = nhanVienRepository.findByTaiKhoan(taiKhoan)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ nhân viên"));

        String email = request.getEmail().trim();

        if (repository.existsByEmailAndIdTaiKhoanNot(
                email,
                taiKhoan.getIdTaiKhoan()
        )) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        if (nhanVienRepository.existsByEmailAndIdNhanVienNot(
                email,
                nhanVien.getIdNhanVien()
        )) {
            throw new RuntimeException("Email đã được sử dụng bởi nhân viên khác");
        }

        nhanVien.setTenNhanVien(request.getTenKhachHang().trim());
        nhanVien.setEmail(email);
        nhanVien.setGioiTinh(request.getGioiTinh());
        nhanVien.setNgaySinh(request.getNgaySinh());
        nhanVien.setSdt(request.getSdt().trim());
        taiKhoan.setEmail(email);

        nhanVienRepository.save(nhanVien);
        repository.save(taiKhoan);
    }
    @Transactional
    public void updateCustomerProfile(
            TaiKhoan taiKhoan,
            CustomerProfileUpdateRequest request
    ) {
        if (!"USER".equals(taiKhoan.getRole())) {
            throw new RuntimeException("Chức năng này chỉ áp dụng cho khách hàng");
        }

        KhachHang khachHang = khachHangRepository.findByTaiKhoan(taiKhoan)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ khách hàng"));

        String email = request.getEmail().trim();

        if (repository.existsByEmailAndIdTaiKhoanNot(
                email,
                taiKhoan.getIdTaiKhoan()
        )) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        khachHang.setTenKhachHang(request.getTenKhachHang().trim());
        khachHang.setEmail(email);
        khachHang.setSdt(request.getSdt().trim());
        khachHang.setGioiTinh(request.getGioiTinh());
        khachHang.setNgaySinh(request.getNgaySinh());

        taiKhoan.setEmail(email);

        khachHangRepository.save(khachHang);
        repository.save(taiKhoan);
    }
    @Transactional
    public void changePhone(
            TaiKhoan taiKhoan,
            ChangePhoneRequest request
    ) {
        if (!passwordEncoder.matches(
                request.getPassword(),
                taiKhoan.getPassword()
        )) {
            throw new RuntimeException("Mật khẩu hiện tại không chính xác");
        }

        String newPhone = request.getNewPhone().trim();

        if (newPhone.equals(taiKhoan.getUsername())) {
            throw new RuntimeException("Số điện thoại mới phải khác số điện thoại hiện tại");
        }

        if (repository.existsByUsernameAndIdTaiKhoanNot(
                newPhone,
                taiKhoan.getIdTaiKhoan()
        )) {
            throw new RuntimeException("Số điện thoại đã được sử dụng");
        }

        taiKhoan.setUsername(newPhone);
        repository.save(taiKhoan);
    }
}