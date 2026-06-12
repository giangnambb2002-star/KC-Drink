package com.example.datn.auth.service;
import com.example.datn.auth.dto.LoginRequest;
import com.example.datn.auth.dto.MeResponse;
import com.example.datn.auth.dto.RegisterRequest;
import com.example.datn.auth.dto.ChangePasswordRequest;
import com.example.datn.khach_hang.repository.KhachHangRepository;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import com.example.datn.tai_khoan.repository.TaiKhoanRepository;
import com.example.datn.nhan_vien.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TaiKhoanRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final NhanVienRepository nhanVienRepository;

    private final KhachHangRepository khachHangRepository;

    public void register(RegisterRequest request) {

        if (repository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        TaiKhoan taiKhoan = new TaiKhoan();

        taiKhoan.setUsername(request.getUsername());
        taiKhoan.setEmail(request.getEmail());

        // Mã hóa mật khẩu
        taiKhoan.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        taiKhoan.setRole("USER");
        taiKhoan.setTrangThai(1);

        repository.save(taiKhoan);
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
    public MeResponse getCurrentUser(
            TaiKhoan taiKhoan
    ) {
        String tenNguoiDung = null;
        String chucVu = null;
        Integer diemTichLuy = null;
        if ("STAFF".equals(taiKhoan.getRole())) {
            var nhanVien = nhanVienRepository.findByTaiKhoan(taiKhoan);
            if (nhanVien.isPresent()) {
                tenNguoiDung = nhanVien.get().getTenNhanVien();
                chucVu = nhanVien.get().getChucVu();
            }
        }
        if ("USER".equals(taiKhoan.getRole())) {
            var khachHang = khachHangRepository.findByTaiKhoan(taiKhoan);
            if (khachHang.isPresent()) {
                tenNguoiDung = khachHang.get().getTenKhachHang();
                diemTichLuy = khachHang.get().getDiemTichLuy();
            }
        }
        return new MeResponse(
                taiKhoan.getIdTaiKhoan(),
                taiKhoan.getUsername(),
                taiKhoan.getEmail(),
                taiKhoan.getRole(),
                tenNguoiDung,
                chucVu,
                diemTichLuy
        );
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
}
