package com.example.datn.voucher.service;

import com.example.datn.common.PageResponse;
import com.example.datn.khach_hang.repository.KhachHangRepository;
import com.example.datn.nhat_ky_he_thong.service.NhatKyHeThongService;
import com.example.datn.voucher.dto.VoucherRequest;
import com.example.datn.voucher.dto.VoucherResponse;
import com.example.datn.voucher.dto.VoucherValidationResponse;
import com.example.datn.voucher.entity.Voucher;
import com.example.datn.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository repository;
    private final KhachHangRepository khachHangRepository;
    private final VoucherMailService mailService;
    private final NhatKyHeThongService nhatKyHeThongService;

    // Danh sách phân trang cho Admin
    public PageResponse<VoucherResponse> getAll(
            String keyword, // Tạm thời để trống logic search, bạn có thể tự thêm trong Repo sau
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

        // Lấy tất cả có phân trang
        Page<Voucher> pageData = repository.searchVoucher(keyword, trangThai, pageable);

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


    // Logic tính tiền giảm giá cho Khách hàng
    public VoucherValidationResponse kiemTraVoucher(VoucherRequest request) {
        Optional<Voucher> optionalVoucher = repository.findByMaVoucher(request.getMaVoucher().trim());

        if (optionalVoucher.isEmpty()) {
            return new VoucherValidationResponse(false, BigDecimal.ZERO, "Mã giảm giá không tồn tại!");
        }

        Voucher voucher = optionalVoucher.get();

        if (!voucher.getIdKhachHang().equals(request.getIdKhachHang())) {
            return new VoucherValidationResponse(false, BigDecimal.ZERO, "Mã giảm giá này không thuộc về tài khoản của bạn!");
        }

        if (voucher.getTrangThai() == 0) {
            return new VoucherValidationResponse(false, BigDecimal.ZERO, "Mã giảm giá này đã được sử dụng!");
        }
        if (voucher.getSoLuong() != null && voucher.getSoLuong() <= 0) {
            return new VoucherValidationResponse(false, BigDecimal.ZERO, "Mã giảm giá này đã hết số lượng sử dụng!");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getNgayBatDau()) || now.isAfter(voucher.getNgayKetThuc())) {
            return new VoucherValidationResponse(false, BigDecimal.ZERO, "Mã giảm giá đã hết hạn hoặc chưa đến thời gian sử dụng!");
        }

        if (request.getTongTienDonHang().compareTo(voucher.getDieuKien()) < 0) {
            return new VoucherValidationResponse(false, BigDecimal.ZERO, "Đơn hàng chưa đạt giá trị tối thiểu!");
        }

        BigDecimal soTienGiam = BigDecimal.ZERO;

        if ("PERCENT".equals(voucher.getLoaiVoucher())) {
            soTienGiam = request.getTongTienDonHang()
                    .multiply(voucher.getGiaTriGiam())
                    .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);

            if (voucher.getGiamToiDa() != null && soTienGiam.compareTo(voucher.getGiamToiDa()) > 0) {
                soTienGiam = voucher.getGiamToiDa();
            }
        } else if ("FIXED".equals(voucher.getLoaiVoucher())) {
            soTienGiam = voucher.getGiaTriGiam();
        }

        if (soTienGiam.compareTo(request.getTongTienDonHang()) > 0) {
            soTienGiam = request.getTongTienDonHang();
        }

        return new VoucherValidationResponse(true, soTienGiam, "Áp dụng mã giảm giá thành công!");
    }

    // Hàm chuyển đổi Entity -> DTO
    private VoucherResponse toResponse(Voucher voucher) {
        VoucherResponse response = new VoucherResponse();
        response.setIdVoucher(voucher.getIdVoucher());
        response.setMaVoucher(voucher.getMaVoucher());
        response.setTenVoucher(voucher.getTenVoucher());
        response.setGiaTriGiam(voucher.getGiaTriGiam());
        response.setDieuKien(voucher.getDieuKien());
        response.setNgayBatDau(voucher.getNgayBatDau());
        response.setNgayKetThuc(voucher.getNgayKetThuc());
        response.setSoLuong(voucher.getSoLuong());
        response.setTrangThai(voucher.getTrangThai());
        response.setIdKhachHang(voucher.getIdKhachHang());
        response.setLoaiVoucher(voucher.getLoaiVoucher());
        response.setGiamToiDa(voucher.getGiamToiDa());
        return response;
    }

    // ==========================================
    // CÁC HÀM THÊM MỚI (CREATE, UPDATE, LOCK, UNLOCK)
    // ==========================================

    public VoucherResponse create(VoucherRequest request) {
        // 1. Chặn trùng mã Voucher
        if (repository.findByMaVoucher(request.getMaVoucher().trim().toUpperCase()).isPresent()) {
            throw new RuntimeException("Mã giảm giá này đã tồn tại trong hệ thống!");
        }
        if (request.getNgayBatDau() != null && request.getNgayKetThuc() != null) {
            if (request.getNgayBatDau().isAfter(request.getNgayKetThuc())) {
                throw new RuntimeException("Thời gian bắt đầu không thể nằm sau thời gian kết thúc!");
            }
        }

        Voucher voucher = new Voucher();
        voucher.setMaVoucher(request.getMaVoucher().trim().toUpperCase());
        voucher.setTenVoucher(request.getTenVoucher());
        voucher.setDieuKien(request.getDieuKien());
        voucher.setNgayBatDau(request.getNgayBatDau());
        voucher.setNgayKetThuc(request.getNgayKetThuc());
        voucher.setSoLuong(request.getSoLuong());
        voucher.setIdKhachHang(request.getIdKhachHang());
        voucher.setTrangThai(1); // Mặc định tạo ra là Đang mở (1)

        // 3. Mapping dữ liệu từ Giao diện Vue xuống Database
        // Lưu ý: Nếu file VoucherRequest.java của bạn dùng tên biến khác, hãy sửa lại cho khớp nhé!
        voucher.setGiaTriGiam(request.getGiamGia());

        if (request.getKieuGiamGia() != null) {
            // Chuyển 0 (Bên Vue) thành "PERCENT", 1 thành "FIXED"
            voucher.setLoaiVoucher(request.getKieuGiamGia() == 0 ? "PERCENT" : "FIXED");
        }

        Voucher savedVoucher = repository.save(voucher);
        nhatKyHeThongService.ghiLogCurrentUser(
                "THÊM",
                "VOUCHER",
                savedVoucher.getIdVoucher(),
                "Thêm voucher " + savedVoucher.getMaVoucher()
        );

        // NẾU CÓ NHẬP ID KHÁCH HÀNG -> TỰ ĐỘNG GỬI MAIL
        if (savedVoucher.getIdKhachHang() != null) {
            khachHangRepository.findById(savedVoucher.getIdKhachHang()).ifPresent(kh -> {
                if (kh.getEmail() != null && !kh.getEmail().isEmpty()) {
                    // Gọi hàm gửi mail vừa viết ở Bước 1
                    mailService.guiMailTangVoucherRieng(kh.getEmail(), kh.getTenKhachHang(), savedVoucher);
                }
            });
        }
        return toResponse(savedVoucher);
    }

    public VoucherResponse update(Integer id, VoucherRequest request) {
        Voucher voucher = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá!"));

        // Kiểm tra trùng mã (phải đảm bảo mã đó không thuộc về 1 voucher ID khác)
        Optional<Voucher> existing = repository.findByMaVoucher(request.getMaVoucher().trim().toUpperCase());
        if (existing.isPresent() && !existing.get().getIdVoucher().equals(id)) {
            throw new RuntimeException("Mã giảm giá này đã được sử dụng cho chương trình khác!");
        }

        if (request.getNgayBatDau() != null && request.getNgayKetThuc() != null) {
            if (request.getNgayBatDau().isAfter(request.getNgayKetThuc())) {
                throw new RuntimeException("Thời gian bắt đầu không thể nằm sau thời gian kết thúc!");
            }
        }

        voucher.setMaVoucher(request.getMaVoucher().trim().toUpperCase());
        voucher.setTenVoucher(request.getTenVoucher());
        voucher.setDieuKien(request.getDieuKien());
        voucher.setNgayBatDau(request.getNgayBatDau());
        voucher.setNgayKetThuc(request.getNgayKetThuc());
        voucher.setSoLuong(request.getSoLuong());
        voucher.setIdKhachHang(request.getIdKhachHang());

        voucher.setGiaTriGiam(request.getGiamGia());
        if (request.getKieuGiamGia() != null) {
            voucher.setLoaiVoucher(request.getKieuGiamGia() == 0 ? "PERCENT" : "FIXED");
        }
        Voucher savedVoucher = repository.save(voucher);

        nhatKyHeThongService.ghiLogCurrentUser(
                "CẬP NHẬT",
                "VOUCHER",
                savedVoucher.getIdVoucher(),
                "Cập nhật voucher " + savedVoucher.getMaVoucher()
        );

        return toResponse(savedVoucher);
    }

    public VoucherResponse lock(Integer id) {
        Voucher voucher = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá!"));
        voucher.setTrangThai(0); // Chuyển sang Đã khóa
        Voucher savedVoucher = repository.save(voucher);

        nhatKyHeThongService.ghiLogCurrentUser(
                "KHÓA",
                "VOUCHER",
                savedVoucher.getIdVoucher(),
                "Khóa voucher " + savedVoucher.getMaVoucher()
        );

        return toResponse(savedVoucher);
    }

    public VoucherResponse unlock(Integer id) {
        Voucher voucher = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá!"));
        voucher.setTrangThai(1); // Chuyển sang Đang mở
        Voucher savedVoucher = repository.save(voucher);

        nhatKyHeThongService.ghiLogCurrentUser(
                "MỞ KHÓA",
                "VOUCHER",
                savedVoucher.getIdVoucher(),
                "Mở khóa voucher " + savedVoucher.getMaVoucher()
        );

        return toResponse(savedVoucher);
    }
    // ====================================================================
    // CRON JOB: TỰ ĐỘNG QUÉT VÀ KHÓA VOUCHER ĐÃ HẾT HẠN
    // Chạy vào 00:00 mỗi đêm VÀ chạy ngay lập tức khi bật Server (Local)
    // ====================================================================
    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 0 * * ?")
    @jakarta.annotation.PostConstruct
    public void tuDongKhoaVoucherHetHan() {
        System.out.println("============== [QUÉT VOUCHER HẾT HẠN] ==============");
        try {
            java.util.List<Voucher> danhSachVoucher = repository.findAll();

            java.time.LocalDateTime bayGio = java.time.LocalDateTime.now();
            int soLuongDaKhoa = 0;

            for (Voucher v : danhSachVoucher) {
                if (v.getTrangThai() == 1
                        && v.getNgayKetThuc() != null
                        && v.getNgayKetThuc().isBefore(bayGio)) {
                    v.setTrangThai(0);
                    repository.save(v);
                    soLuongDaKhoa++;
                }
            }
            if (soLuongDaKhoa > 0) {
                nhatKyHeThongService.ghiLogHeThong(
                        "TỰ ĐỘNG KHÓA",
                        "VOUCHER",
                        null,
                        "Hệ thống đã tự động khóa "
                                + soLuongDaKhoa
                                + " voucher hết hạn"
                );
            }
            System.out.println("✅ TỰ ĐỘNG KHÓA THÀNH CÔNG: " + soLuongDaKhoa + " MÃ QUÁ HẠN.");
        } catch (Exception e) {
            System.out.println("❌ LỖI KHI QUÉT VOUCHER: " + e.getMessage());
        }
        System.out.println("====================================================");
    }
}