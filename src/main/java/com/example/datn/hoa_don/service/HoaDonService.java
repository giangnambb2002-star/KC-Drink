package com.example.datn.hoa_don.service;

import com.example.datn.ban_thanh_pham.entity.CongThucSanPhamBtp;
import com.example.datn.ban_thanh_pham.repository.CongThucSanPhamBtpRepository;
import com.example.datn.ban_thanh_pham.service.BanThanhPhamKhoService;
import com.example.datn.common.PageResponse;
import com.example.datn.hoa_don.dto.ApDungVoucherRequest;
import com.example.datn.hoa_don.dto.CapNhatKhachHangHoaDonRequest;
import com.example.datn.hoa_don.dto.CapNhatSoLuongRequest;
import com.example.datn.hoa_don.dto.CapNhatToppingRequest;
import com.example.datn.hoa_don.dto.HdctToppingResponse;
import com.example.datn.hoa_don.dto.HoaDonChiTietResponse;
import com.example.datn.hoa_don.dto.HoaDonListResponse;
import com.example.datn.hoa_don.dto.HoaDonResponse;
import com.example.datn.hoa_don.dto.TaoHoaDonOfflineRequest;
import com.example.datn.hoa_don.dto.ThanhToanHoaDonRequest;
import com.example.datn.hoa_don.dto.ThemMonRequest;
import com.example.datn.hoa_don.dto.ThemToppingRequest;
import com.example.datn.hoa_don.dto.VoucherKhaDungResponse;
import com.example.datn.hoa_don.entity.HdctTopping;
import com.example.datn.hoa_don.entity.HoaDon;
import com.example.datn.hoa_don.entity.HoaDonChiTiet;
import com.example.datn.hoa_don.repository.HdctToppingRepository;
import com.example.datn.hoa_don.repository.HoaDonChiTietRepository;
import com.example.datn.hoa_don.repository.HoaDonRepository;
import com.example.datn.khach_hang.entity.KhachHang;
import com.example.datn.khach_hang.repository.KhachHangRepository;
import com.example.datn.nguyen_lieu.entity.CongThucSanPham;
import com.example.datn.nguyen_lieu.repository.CongThucSanPhamRepository;
import com.example.datn.nguyen_lieu.service.KhoService;
import com.example.datn.nhan_vien.entity.NhanVien;
import com.example.datn.nhan_vien.repository.NhanVienRepository;
import com.example.datn.payos.dto.PayOSCreateResponse;
import com.example.datn.payos.dto.PayOSPaymentStatusResponse;
import com.example.datn.payos.service.PayOSService;
import com.example.datn.san_pham.entity.SanPham;
import com.example.datn.san_pham.entity.SanPhamSize;
import com.example.datn.san_pham.entity.Size;
import com.example.datn.san_pham.repository.SanPhamRepository;
import com.example.datn.san_pham.repository.SanPhamSizeRepository;
import com.example.datn.san_pham.repository.SizeRepository;
import com.example.datn.topping.entity.Topping;
import com.example.datn.topping.repository.LoToppingRepository;
import com.example.datn.topping.repository.ToppingRepository;
import com.example.datn.topping.service.ToppingKhoService;
import com.example.datn.van_chuyen.entity.VanDonGhn;
import com.example.datn.van_chuyen.repository.VanDonGhnRepository;
import com.example.datn.van_chuyen.service.VanDonGhnService;
import com.example.datn.voucher.entity.Voucher;
import com.example.datn.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HoaDonService {
    private final HoaDonRepository repository;
    private final HoaDonChiTietRepository chiTietRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final CongThucSanPhamBtpRepository congThucSanPhamBtpRepository;
    private final BanThanhPhamKhoService banThanhPhamKhoService;
    private final CongThucSanPhamRepository congThucSanPhamRepository;
    private final KhoService khoService;
    private final HdctToppingRepository hdctToppingRepository;
    private final ToppingKhoService toppingKhoService;
    private final SanPhamRepository sanPhamRepository;
    private final SizeRepository sizeRepository;
    private final SanPhamSizeRepository sanPhamSizeRepository;
    private final LoToppingRepository loToppingRepository;
    private final ToppingRepository toppingRepository;
    private final VoucherRepository voucherRepository;
    private final PayOSService payOSService;
    private final VanDonGhnService vanDonGhnService;
    private final VanDonGhnRepository vanDonGhnRepository;

    public HoaDonResponse getById(Integer id) {
        HoaDon hoaDon = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        return toResponse(hoaDon);
    }
    @Transactional
    public HoaDonResponse taoHoaDonOffline(TaoHoaDonOfflineRequest request) {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon(taoMaHoaDon());
        hoaDon.setLoaiHoaDon("OFFLINE");
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setTongTien(BigDecimal.ZERO);
        hoaDon.setGiamGia(BigDecimal.ZERO);
        hoaDon.setPhiVanChuyen(BigDecimal.ZERO);
        hoaDon.setThanhTien(BigDecimal.ZERO);
        hoaDon.setTrangThai("CHO_THANH_TOAN");
        hoaDon.setGhiChu(request.getGhiChu());
        if (request.getIdKhachHang() != null) {
            KhachHang khachHang = khachHangRepository.findById(request.getIdKhachHang())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
            hoaDon.setKhachHang(khachHang);
        }
        if (request.getIdNhanVien() != null) {
            NhanVien nhanVien = nhanVienRepository.findById(request.getIdNhanVien())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
            hoaDon.setNhanVien(nhanVien);
        }
        return toResponse(repository.save(hoaDon));
    }
    @Transactional
    public HoaDonResponse themMon(Integer idHoaDon, ThemMonRequest request) {
        HoaDon hoaDon = getHoaDonChoThanhToanDeSua(idHoaDon);
        if (request.getIdSanPham() == null) {
            throw new RuntimeException("Sản phẩm không được để trống");
        }
        if (request.getIdSize() == null) {
            throw new RuntimeException("Size không được để trống");
        }
        if (request.getSoLuong() == null || request.getSoLuong() <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }
        if (request.getMucDuong() != null
                && request.getMucDuong() != 0
                && request.getMucDuong() != 30
                && request.getMucDuong() != 50
                && request.getMucDuong() != 70
                && request.getMucDuong() != 100) {
            throw new RuntimeException("Mức đường không hợp lệ");
        }
        if (request.getMucDa() != null
                && request.getMucDa() != 0
                && request.getMucDa() != 50
                && request.getMucDa() != 100) {
            throw new RuntimeException("Mức đá không hợp lệ");
        }
        SanPham sanPham = sanPhamRepository.findById(request.getIdSanPham())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        if (sanPham.getTrangThai() == null || sanPham.getTrangThai() != 1) {
            throw new RuntimeException("Sản phẩm đang ngừng bán");
        }
        SanPhamSize sanPhamSize = sanPhamSizeRepository
                .findByIdSanPhamAndSize_IdSize(
                        request.getIdSanPham(),
                        request.getIdSize()
                )
                .orElseThrow(() ->
                        new RuntimeException("Sản phẩm không hỗ trợ size này"));
        BigDecimal giaGoc = sanPham.getGia() == null
                ? BigDecimal.ZERO
                : sanPham.getGia();
        BigDecimal phuThu = sanPhamSize.getPhuThu() == null
                ? BigDecimal.ZERO
                : sanPhamSize.getPhuThu();
        BigDecimal donGia = giaGoc.add(phuThu);
        int mucDuong = request.getMucDuong() != null
                ? request.getMucDuong()
                : 100;
        int mucDa = request.getMucDa() != null
                ? request.getMucDa()
                : 100;
        String ghiChu = request.getGhiChu() != null
                && !request.getGhiChu().trim().isEmpty()
                ? request.getGhiChu().trim()
                : null;
        HoaDonChiTiet chiTiet = chiTietRepository.findMonTrung(
                idHoaDon,
                request.getIdSanPham(),
                request.getIdSize(),
                mucDuong,
                mucDa,
                ghiChu,
                donGia
        ).orElse(null);
        if (chiTiet != null
                && chiTiet.getDonGia() != null
                && chiTiet.getDonGia().compareTo(donGia) != 0) {
            chiTiet = null;
        }
        if (chiTiet != null) {
            int soLuongMoi = chiTiet.getSoLuong() + request.getSoLuong();
            chiTiet.setSoLuong(soLuongMoi);
            chiTiet.setThanhTien(
                    chiTiet.getDonGia()
                            .multiply(BigDecimal.valueOf(soLuongMoi))
            );
        } else {
            chiTiet = new HoaDonChiTiet();
            chiTiet.setHoaDon(hoaDon);
            chiTiet.setIdSanPham(request.getIdSanPham());
            chiTiet.setIdSize(request.getIdSize());
            chiTiet.setMucDuong(mucDuong);
            chiTiet.setMucDa(mucDa);
            chiTiet.setGhiChu(ghiChu);
            chiTiet.setSoLuong(request.getSoLuong());
            chiTiet.setDonGia(donGia);
            chiTiet.setThanhTien(
                    donGia.multiply(BigDecimal.valueOf(request.getSoLuong()))
            );
        }
        chiTietRepository.save(chiTiet);
        tinhLaiTongTien(hoaDon);
        return toResponse(hoaDon);
    }
    @Transactional
    public HoaDonResponse capNhatSoLuong(
            Integer idChiTiet,
            CapNhatSoLuongRequest request
    ) {
        HoaDonChiTiet chiTiet = chiTietRepository.findById(idChiTiet)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết hóa đơn"));
        HoaDon hoaDon = getHoaDonChoThanhToanDeSua(
                chiTiet.getHoaDon().getIdHoaDon()
        );
        if (request.getSoLuong() == null || request.getSoLuong() <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }
        chiTiet.setSoLuong(request.getSoLuong());
        chiTiet.setThanhTien(
                chiTiet.getDonGia()
                        .multiply(BigDecimal.valueOf(request.getSoLuong()))
        );
        chiTietRepository.save(chiTiet);
        tinhLaiTongTien(hoaDon);
        return toResponse(hoaDon);
    }
    @Transactional
    public HoaDonResponse xoaMon(Integer idChiTiet) {
        HoaDonChiTiet chiTiet = chiTietRepository.findById(idChiTiet)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết hóa đơn"));
        HoaDon hoaDon = getHoaDonChoThanhToanDeSua(
                chiTiet.getHoaDon().getIdHoaDon()
        );
        List<HdctTopping> toppingList =
                hdctToppingRepository.findByHoaDonChiTiet_IdHoaDonChiTiet(idChiTiet);
        if (!toppingList.isEmpty()) {
            hdctToppingRepository.deleteAll(toppingList);
            hdctToppingRepository.flush();
        }
        chiTietRepository.delete(chiTiet);
        chiTietRepository.flush();
        tinhLaiTongTien(hoaDon);
        return toResponse(hoaDon);
    }
    private HoaDon getHoaDonChoThanhToan(Integer idHoaDon) {
        return kiemTraHoaDonChoThanhToan(repository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn")));
    }
    private HoaDon getHoaDonChoThanhToanCoKhoa(Integer idHoaDon) {
        return kiemTraHoaDonChoThanhToan(repository.findByIdForUpdate(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn")));
    }
    private HoaDon getHoaDonChoThanhToanDeSua(Integer idHoaDon) {
        HoaDon hoaDon = getHoaDonChoThanhToanCoKhoa(idHoaDon);
        if (payOSDangKhoaHoaDon(hoaDon)) {
            throw new RuntimeException(
                    "Hóa đơn đang có giao dịch PayOS chờ thanh toán, không thể chỉnh sửa"
            );
        }
        return hoaDon;
    }
    private HoaDon kiemTraHoaDonChoThanhToan(HoaDon hoaDon) {
        if (!"CHO_THANH_TOAN".equals(hoaDon.getTrangThai())) {
            throw new RuntimeException(
                    "Hóa đơn không còn ở trạng thái chờ thanh toán"
            );
        }
        return hoaDon;
    }
    private void tinhLaiTongTien(HoaDon hoaDon) {
        List<HoaDonChiTiet> chiTietList = chiTietRepository.findByHoaDon_IdHoaDon(hoaDon.getIdHoaDon());
        BigDecimal tongTien = BigDecimal.ZERO;
        for (HoaDonChiTiet chiTiet : chiTietList) {
            if (chiTiet.getThanhTien() != null) tongTien = tongTien.add(chiTiet.getThanhTien());
            List<HdctTopping> toppingList =
                    hdctToppingRepository.findByHoaDonChiTiet_IdHoaDonChiTiet(chiTiet.getIdHoaDonChiTiet());
            BigDecimal tienTopping = toppingList.stream()
                    .map(HdctTopping::getThanhTien)
                    .filter(value -> value != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            tongTien = tongTien.add(tienTopping);
        }
        tongTien = tongTien.setScale(0, RoundingMode.HALF_UP);
        hoaDon.setTongTien(tongTien);
        BigDecimal giamGia = BigDecimal.ZERO;
        Voucher voucher = hoaDon.getVoucher();
        if (voucher != null) {
            if (voucher.getIdKhachHang() != null && (hoaDon.getKhachHang() == null || !voucher.getIdKhachHang().equals(hoaDon.getKhachHang().getIdKhachHang()))) {
                hoaDon.setVoucher(null);
            } else if (tongTien.compareTo(BigDecimal.ZERO) <= 0) {
                hoaDon.setVoucher(null);
            } else if (voucher.getDieuKien() != null && tongTien.compareTo(voucher.getDieuKien()) < 0) {
                hoaDon.setVoucher(null);
            } else {
                giamGia = tinhTienGiamVoucher(voucher, tongTien);
            }
        }
        hoaDon.setGiamGia(giamGia);
        BigDecimal phiVanChuyen = hoaDon.getPhiVanChuyen() == null
                ? BigDecimal.ZERO : hoaDon.getPhiVanChuyen();
        BigDecimal thanhTien = tongTien
                .subtract(giamGia)
                .add(phiVanChuyen)
                .max(BigDecimal.ZERO)
                .setScale(0, RoundingMode.HALF_UP);
        hoaDon.setThanhTien(thanhTien);
        repository.save(hoaDon);
    }
    private BigDecimal tinhTienGiamVoucher(Voucher voucher, BigDecimal tongTien) {
        BigDecimal giamGia = BigDecimal.ZERO;
        if ("PERCENT".equals(voucher.getLoaiVoucher())) {
            giamGia = tongTien
                    .multiply(voucher.getGiaTriGiam())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (voucher.getGiamToiDa() != null
                    && giamGia.compareTo(voucher.getGiamToiDa()) > 0) {
                giamGia = voucher.getGiamToiDa();
            }
        } else if ("FIXED".equals(voucher.getLoaiVoucher())) {
            giamGia = voucher.getGiaTriGiam();
        }
        return giamGia.min(tongTien).setScale(0, RoundingMode.HALF_UP);
    }

    private HoaDonResponse toResponse(HoaDon hoaDon) {
        HoaDonResponse response = new HoaDonResponse();
        response.setIdHoaDon(hoaDon.getIdHoaDon());
        response.setMaHoaDon(hoaDon.getMaHoaDon());
        response.setLoaiHoaDon(hoaDon.getLoaiHoaDon());
        response.setNgayTao(hoaDon.getNgayTao());
        response.setTongTien(hoaDon.getTongTien());
        response.setGiamGia(hoaDon.getGiamGia());
        response.setPhiVanChuyen(hoaDon.getPhiVanChuyen());
        response.setThanhTien(hoaDon.getThanhTien());
        response.setHinhThucThanhToan(hoaDon.getHinhThucThanhToan());
        response.setPayosOrderCode(hoaDon.getPayosOrderCode());
        response.setPayosPaymentLinkId(hoaDon.getPayosPaymentLinkId());
        response.setPayosStatus(hoaDon.getPayosStatus());
        response.setTrangThai(hoaDon.getTrangThai());
        response.setGhiChu(hoaDon.getGhiChu());
        if (hoaDon.getKhachHang() != null) {
            response.setIdKhachHang(
                    hoaDon.getKhachHang().getIdKhachHang()
            );
            response.setTenKhachHang(
                    hoaDon.getKhachHang().getTenKhachHang()
            );
        }
        if (hoaDon.getNhanVien() != null) {
            response.setIdNhanVien(
                    hoaDon.getNhanVien().getIdNhanVien()
            );
            response.setTenNhanVien(
                    hoaDon.getNhanVien().getTenNhanVien()
            );
        }
        if (hoaDon.getVoucher() != null) {
            response.setIdVoucher(hoaDon.getVoucher().getIdVoucher());
            response.setMaVoucher(hoaDon.getVoucher().getMaVoucher());
            response.setTenVoucher(hoaDon.getVoucher().getTenVoucher());
        }
        List<HoaDonChiTietResponse> chiTiet =
                chiTietRepository
                        .findByHoaDon_IdHoaDon(
                                hoaDon.getIdHoaDon()
                        )
                        .stream()
                        .map(this::toChiTietResponse)
                        .toList();
        response.setChiTiet(chiTiet);
        return response;
    }
    private HoaDonChiTietResponse toChiTietResponse(HoaDonChiTiet chiTiet) {
        HoaDonChiTietResponse response = new HoaDonChiTietResponse();
        response.setIdHoaDonChiTiet(chiTiet.getIdHoaDonChiTiet());
        response.setIdSanPham(chiTiet.getIdSanPham());
        SanPham sanPham = sanPhamRepository.findById(chiTiet.getIdSanPham())
                .orElse(null);
        response.setTenSanPham(
                sanPham != null ? sanPham.getTenSanPham() : null
        );
        response.setIdSize(chiTiet.getIdSize());
        Size size = sizeRepository.findById(chiTiet.getIdSize())
                .orElse(null);
        response.setTenSize(
                size != null ? size.getTenSize() : null
        );
        response.setMucDuong(chiTiet.getMucDuong());
        response.setMucDa(chiTiet.getMucDa());
        response.setGhiChu(chiTiet.getGhiChu());
        response.setSoLuong(chiTiet.getSoLuong());
        response.setDonGia(chiTiet.getDonGia());
        response.setThanhTien(chiTiet.getThanhTien());
        List<HdctToppingResponse> toppingList =
                hdctToppingRepository.findByHoaDonChiTiet_IdHoaDonChiTiet(
                        chiTiet.getIdHoaDonChiTiet()
                ).stream()
                        .map(this::toToppingResponse)
                        .toList();
        response.setToppingList(toppingList);
        return response;
    }
    private HdctToppingResponse toToppingResponse(HdctTopping topping) {
        HdctToppingResponse response = new HdctToppingResponse();
        response.setIdHdctTopping(topping.getIdHdctTopping());
        response.setIdTopping(topping.getIdTopping());
        Topping toppingEntity = toppingRepository.findById(topping.getIdTopping())
                .orElse(null);
        response.setTenTopping(
                toppingEntity != null ? toppingEntity.getTenTopping() : null
        );
        response.setSoLuong(topping.getSoLuong());
        response.setDonGia(topping.getDonGia());
        response.setThanhTien(topping.getThanhTien());
        return response;
    }
    private String taoMaHoaDon() {
        return "HD" + System.currentTimeMillis();
    }
    @Transactional
    public HoaDonResponse thanhToanHoaDon(Integer idHoaDon, ThanhToanHoaDonRequest request) {
        HoaDon hoaDon = getHoaDonChoThanhToanCoKhoa(idHoaDon);
        List<HoaDonChiTiet> chiTietList =
                chiTietRepository.findByHoaDon_IdHoaDon(idHoaDon);
        if (chiTietList.isEmpty()) {
            throw new RuntimeException("Hóa đơn chưa có sản phẩm");
        }
        String hinhThucThanhToan = request.getHinhThucThanhToan();
        if (hinhThucThanhToan == null || hinhThucThanhToan.isBlank()) {
            throw new RuntimeException("Vui lòng chọn hình thức thanh toán");
        }
        if (!"TIEN_MAT".equals(hinhThucThanhToan)
                && !"CHUYEN_KHOAN".equals(hinhThucThanhToan)) {
            throw new RuntimeException("Hình thức thanh toán không hợp lệ");
        }
        if ("TIEN_MAT".equals(hinhThucThanhToan)
                && payOSDangKhoaHoaDon(hoaDon)) {
            throw new RuntimeException(
                    "Hóa đơn đang có giao dịch PayOS chưa kết thúc, không thể thanh toán tiền mặt");}
        Voucher voucher = hoaDon.getVoucher();
        if (voucher != null) {
            LocalDateTime now = LocalDateTime.now();
            if (voucher.getTrangThai() == null || voucher.getTrangThai() != 1) {
                throw new RuntimeException("Voucher không còn khả dụng");
            }
            if (voucher.getSoLuong() != null && voucher.getSoLuong() <= 0) {
                throw new RuntimeException("Voucher đã hết lượt sử dụng");
            }
            if (voucher.getNgayBatDau() != null
                    && now.isBefore(voucher.getNgayBatDau())) {
                throw new RuntimeException("Voucher chưa đến thời gian sử dụng");
            }
            if (voucher.getNgayKetThuc() != null
                    && now.isAfter(voucher.getNgayKetThuc())) {
                throw new RuntimeException("Voucher đã hết hạn");
            }
            if (voucher.getIdKhachHang() != null
                    && (hoaDon.getKhachHang() == null
                    || !voucher.getIdKhachHang().equals(
                    hoaDon.getKhachHang().getIdKhachHang()))) {
                throw new RuntimeException(
                        "Voucher không thuộc về khách hàng của hóa đơn"
                );
            }
            if (voucher.getDieuKien() != null
                    && hoaDon.getTongTien().compareTo(voucher.getDieuKien()) < 0) {
                throw new RuntimeException(
                        "Hóa đơn chưa đạt giá trị tối thiểu để dùng voucher"
                );
            }
        }
        if ("CHUYEN_KHOAN".equals(hinhThucThanhToan)) {
            if (hoaDon.getPayosOrderCode() == null) {
                throw new RuntimeException("Hóa đơn chưa có giao dịch PayOS");
            }
            PayOSPaymentStatusResponse paymentInfo =
                    payOSService.getPaymentStatusInfo(hoaDon.getPayosOrderCode());
            Long expectedAmount = hoaDon.getThanhTien().longValueExact();
            if (!expectedAmount.equals(paymentInfo.getAmount())) {
                throw new RuntimeException("Số tiền giao dịch PayOS không khớp với hóa đơn");
            }
            if (!"PAID".equalsIgnoreCase(paymentInfo.getStatus())) {
                throw new RuntimeException("Giao dịch chuyển khoản chưa được thanh toán");
            }
            hoaDon.setPayosStatus(paymentInfo.getStatus());
        }
        // ktra hóa đơn trừ
        kiemTraKhoTruocThanhToan(chiTietList);
        // Sau khi tất cả đều hợp lệ mới bắt đầu trừ kho
        for (HoaDonChiTiet chiTiet : chiTietList) {
            List<CongThucSanPhamBtp> congThucBtpList =
                    congThucSanPhamBtpRepository.findByIdSanPhamAndIdSize(
                            chiTiet.getIdSanPham(),
                            chiTiet.getIdSize());
            for (CongThucSanPhamBtp congThuc : congThucBtpList) {
                BigDecimal soLuongCanTru =
                        congThuc.getSoLuongCanDung()
                                .multiply(BigDecimal.valueOf(chiTiet.getSoLuong()));
                banThanhPhamKhoService.truBanThanhPham(
                        congThuc.getBanThanhPham().getIdBanThanhPham(),
                        soLuongCanTru
                );}
            List<CongThucSanPham> congThucNguyenLieuList =
                    congThucSanPhamRepository.findByIdSanPhamAndIdSize(chiTiet.getIdSanPham(), chiTiet.getIdSize());
            for (CongThucSanPham congThuc : congThucNguyenLieuList) {
                Double soLuongCanTru = congThuc.getSoLuongCanDung() * chiTiet.getSoLuong();
                khoService.truKhoNguyenLieu(congThuc.getNguyenLieu().getIdNguyenLieu(), soLuongCanTru);}
            List<HdctTopping> toppingList =
                    hdctToppingRepository.findByHoaDonChiTiet_IdHoaDonChiTiet(chiTiet.getIdHoaDonChiTiet());
            for (HdctTopping topping : toppingList) {
                toppingKhoService.truKhoTopping(
                        topping.getIdTopping(),
                        topping.getSoLuong());}}
        if (voucher != null && voucher.getSoLuong() != null) {
            int soLuongConLai = voucher.getSoLuong() - 1;
            voucher.setSoLuong(Math.max(soLuongConLai, 0));
            if (soLuongConLai <= 0) {
                voucher.setTrangThai(0);}
            voucherRepository.save(voucher);
        }
        hoaDon.setHinhThucThanhToan(hinhThucThanhToan);
        hoaDon.setTrangThai("DA_THANH_TOAN");
        HoaDon hoaDonDaLuu = repository.save(hoaDon);
        vanDonGhnService.capNhatSauThanhToan(idHoaDon);
        return toResponse(hoaDonDaLuu);
    }
    @Transactional
    public HoaDonResponse themTopping(Integer idChiTiet, ThemToppingRequest request) {
        HoaDonChiTiet chiTiet = chiTietRepository.findById(idChiTiet)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết hóa đơn"));
        HoaDon hoaDon = getHoaDonChoThanhToanDeSua(chiTiet.getHoaDon().getIdHoaDon());
        if (request.getSoLuong() == null || request.getSoLuong() <= 0) {
            throw new RuntimeException("Số lượng topping không hợp lệ");
        }
        if (request.getDonGia() == null || request.getDonGia().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Đơn giá topping không hợp lệ");
        }
        HdctTopping topping = hdctToppingRepository
                .findByHoaDonChiTiet_IdHoaDonChiTietAndIdTopping(
                        idChiTiet, request.getIdTopping()
                )
                .orElse(null);
        int soLuongSauThem = request.getSoLuong();
        if (topping != null) {
            soLuongSauThem += topping.getSoLuong();
        }
        BigDecimal tonKho = loToppingRepository
                .getTongTonKhoConHan(request.getIdTopping());
        long dangChoThanhToan = hdctToppingRepository
                .getTongSoLuongDangChoThanhToan(request.getIdTopping());
        int soLuongHienTaiCuaDong = topping != null ? topping.getSoLuong() : 0;
        long tongSauKhiThem =
                dangChoThanhToan
                        - soLuongHienTaiCuaDong
                        + soLuongSauThem;
        if (BigDecimal.valueOf(tongSauKhiThem).compareTo(tonKho) > 0) {
            long conCoTheThem = tonKho.longValue() - dangChoThanhToan;
            if (topping != null) {
                conCoTheThem += topping.getSoLuong();
            }
            throw new RuntimeException(
                    "Topping chỉ còn " + Math.max(conCoTheThem, 0) +
                            " khả dụng cho các hóa đơn đang chờ"
            );
        }
        if (topping != null) {
            topping.setSoLuong(soLuongSauThem);
            topping.setDonGia(request.getDonGia());
        } else {
            topping = new HdctTopping();
            topping.setHoaDonChiTiet(chiTiet);
            topping.setIdTopping(request.getIdTopping());
            topping.setSoLuong(request.getSoLuong());
            topping.setDonGia(request.getDonGia());
        }
        hdctToppingRepository.save(topping);
        hdctToppingRepository.flush();
        tinhLaiTongTien(hoaDon);
        return toResponse(hoaDon);
    }
    @Transactional
    public HoaDonResponse capNhatTopping(Integer idHdctTopping, CapNhatToppingRequest request) {
        HdctTopping topping = hdctToppingRepository.findById(idHdctTopping)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy topping trong hóa đơn"));
        HoaDon hoaDon = getHoaDonChoThanhToanDeSua(
                topping.getHoaDonChiTiet().getHoaDon().getIdHoaDon()
        );
        if (request.getIdTopping() == null) {
            throw new RuntimeException("Topping không được để trống");
        }
        if (request.getSoLuong() == null || request.getSoLuong() <= 0) {
            throw new RuntimeException("Số lượng topping không hợp lệ");
        }
        if (request.getDonGia() == null ||
                request.getDonGia().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Đơn giá topping không hợp lệ");
        }
        BigDecimal tonKho = loToppingRepository
                .getTongTonKhoConHan(request.getIdTopping());
        long dangChoThanhToan = hdctToppingRepository
                .getTongSoLuongDangChoThanhToan(request.getIdTopping());
        int soLuongHienTaiCuaDong = topping.getSoLuong();
        long tongSauKhiCapNhat =
                dangChoThanhToan
                        - soLuongHienTaiCuaDong
                        + request.getSoLuong();
        if (BigDecimal.valueOf(tongSauKhiCapNhat).compareTo(tonKho) > 0) {
            long conCoTheDung =
                    tonKho.longValue()
                            - dangChoThanhToan
                            + soLuongHienTaiCuaDong;
            throw new RuntimeException(
                    "Topping chỉ còn " + Math.max(conCoTheDung, 0) +
                            " khả dụng cho các hóa đơn đang chờ"
            );
        }
        topping.setIdTopping(request.getIdTopping());
        topping.setSoLuong(request.getSoLuong());
        topping.setDonGia(request.getDonGia());
        hdctToppingRepository.save(topping);
        hdctToppingRepository.flush();
        tinhLaiTongTien(hoaDon);
        return toResponse(hoaDon);
    }
    @Transactional
    public HoaDonResponse xoaTopping(Integer idHdctTopping) {
        HdctTopping topping = hdctToppingRepository.findById(idHdctTopping)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy topping trong hóa đơn"));
        HoaDon hoaDon = getHoaDonChoThanhToanDeSua(
                topping.getHoaDonChiTiet().getHoaDon().getIdHoaDon()
        );
        hdctToppingRepository.delete(topping);
        hdctToppingRepository.flush();
        tinhLaiTongTien(hoaDon);
        return toResponse(hoaDon);
    }
    @Transactional
    public HoaDonResponse huyHoaDon(Integer idHoaDon) {
        HoaDon hoaDon = getHoaDonChoThanhToanDeSua(idHoaDon);
        hoaDon.setTrangThai("DA_HUY");
        HoaDon hoaDonDaLuu = repository.save(hoaDon);
        vanDonGhnService.capNhatSauHuyHoaDon(idHoaDon);
        return toResponse(hoaDonDaLuu);
    }
    @Transactional
    public HoaDonResponse capNhatKhachHang(
            Integer idHoaDon,
            CapNhatKhachHangHoaDonRequest request
    ) {
        HoaDon hoaDon = getHoaDonChoThanhToanDeSua(idHoaDon);
        Integer idKhachHangHienTai = hoaDon.getKhachHang() == null
                ? null : hoaDon.getKhachHang().getIdKhachHang();
        if (vanDonGhnService.coThongTinGiaoHang(idHoaDon)
                && !Objects.equals(idKhachHangHienTai, request.getIdKhachHang())) {
            throw new RuntimeException(
                    "Vui lòng bỏ giao hàng trước khi thay đổi khách hàng"
            );
        }
        if (request.getIdKhachHang() == null) {
            hoaDon.setKhachHang(null);
            tinhLaiTongTien(hoaDon);
            return toResponse(hoaDon);
        }
        KhachHang khachHang = khachHangRepository.findById(request.getIdKhachHang())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        if (khachHang.getTrangThai() == null || khachHang.getTrangThai() != 1) {
            throw new RuntimeException("Khách hàng đang bị khóa");
        }
        hoaDon.setKhachHang(khachHang);
        tinhLaiTongTien(hoaDon);
        return toResponse(hoaDon);
    }
    @Transactional
    public HoaDonResponse apDungVoucher(Integer idHoaDon, ApDungVoucherRequest request) {
        HoaDon hoaDon = getHoaDonChoThanhToanDeSua(idHoaDon);
        if (request.getMaVoucher() == null || request.getMaVoucher().trim().isEmpty()) {
            throw new RuntimeException("Vui lòng nhập mã voucher");
        }
        if (hoaDon.getTongTien() == null || hoaDon.getTongTien().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Hóa đơn chưa có sản phẩm");
        }
        Voucher voucher = voucherRepository
                .findByMaVoucher(request.getMaVoucher().trim().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));
        if (voucher.getTrangThai() == null || voucher.getTrangThai() != 1) {
            throw new RuntimeException("Voucher không còn khả dụng");
        }
        if (voucher.getSoLuong() != null && voucher.getSoLuong() <= 0) {
            throw new RuntimeException("Voucher đã hết lượt sử dụng");
        }
        LocalDateTime now = LocalDateTime.now();
        if (voucher.getNgayBatDau() != null && now.isBefore(voucher.getNgayBatDau())) {
            throw new RuntimeException("Voucher chưa đến thời gian sử dụng");
        }
        if (voucher.getNgayKetThuc() != null && now.isAfter(voucher.getNgayKetThuc())) {
            throw new RuntimeException("Voucher đã hết hạn");
        }
        if (voucher.getIdKhachHang() != null) {
            if (hoaDon.getKhachHang() == null ||
                    !voucher.getIdKhachHang().equals(hoaDon.getKhachHang().getIdKhachHang())) {
                throw new RuntimeException("Voucher không thuộc về khách hàng của hóa đơn");
            }
        }
        BigDecimal tongTien = hoaDon.getTongTien();
        if (voucher.getDieuKien() != null &&
                tongTien.compareTo(voucher.getDieuKien()) < 0) {
            throw new RuntimeException("Hóa đơn chưa đạt giá trị tối thiểu để dùng voucher");
        }
        BigDecimal soTienGiam = BigDecimal.ZERO;
        if ("PERCENT".equals(voucher.getLoaiVoucher())) {
            soTienGiam = tongTien
                    .multiply(voucher.getGiaTriGiam())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (voucher.getGiamToiDa() != null &&
                    soTienGiam.compareTo(voucher.getGiamToiDa()) > 0) {
                soTienGiam = voucher.getGiamToiDa();
            }
        } else if ("FIXED".equals(voucher.getLoaiVoucher())) {
            soTienGiam = voucher.getGiaTriGiam();
        } else {
            throw new RuntimeException("Loại voucher không hợp lệ");
        }
        soTienGiam = soTienGiam
                .min(tongTien)
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal phiVanChuyen = hoaDon.getPhiVanChuyen() == null
                ? BigDecimal.ZERO : hoaDon.getPhiVanChuyen();
        hoaDon.setVoucher(voucher);
        hoaDon.setGiamGia(soTienGiam);
        hoaDon.setThanhTien(
                tongTien.subtract(soTienGiam)
                        .add(phiVanChuyen)
                        .max(BigDecimal.ZERO)
                        .setScale(0, RoundingMode.HALF_UP)
        );
        return toResponse(repository.save(hoaDon));
    }
    @Transactional
    public HoaDonResponse boVoucher(Integer idHoaDon) {
        HoaDon hoaDon = getHoaDonChoThanhToanDeSua(idHoaDon);
        hoaDon.setVoucher(null);
        hoaDon.setGiamGia(BigDecimal.ZERO);
        BigDecimal tongTien = hoaDon.getTongTien() != null
                ? hoaDon.getTongTien()
                : BigDecimal.ZERO;
        BigDecimal phiVanChuyen = hoaDon.getPhiVanChuyen() != null
                ? hoaDon.getPhiVanChuyen()
                : BigDecimal.ZERO;
        hoaDon.setThanhTien(
                tongTien.add(phiVanChuyen)
        );
        return toResponse(repository.save(hoaDon));
    }
    public Page<VoucherKhaDungResponse> getVoucherKhaDung(Integer idHoaDon, int page, int size) {
        HoaDon hoaDon = getHoaDonChoThanhToan(idHoaDon);
        BigDecimal tongTien = hoaDon.getTongTien() != null ? hoaDon.getTongTien() : BigDecimal.ZERO;
        if (tongTien.compareTo(BigDecimal.ZERO) <= 0) {
            return Page.empty(PageRequest.of(page, size));
        }
        Integer idKhachHang = hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getIdKhachHang() : null;
        List<VoucherKhaDungResponse> result = voucherRepository.findVoucherKhaDung(
                tongTien,
                idKhachHang,
                LocalDateTime.now()
        ).stream().map(voucher -> {
            VoucherKhaDungResponse response = new VoucherKhaDungResponse();
            response.setIdVoucher(voucher.getIdVoucher());
            response.setMaVoucher(voucher.getMaVoucher());
            response.setTenVoucher(voucher.getTenVoucher());
            response.setLoaiVoucher(voucher.getLoaiVoucher());
            response.setGiaTriGiam(voucher.getGiaTriGiam());
            response.setGiamToiDa(voucher.getGiamToiDa());
            response.setDieuKien(voucher.getDieuKien());
            response.setSoLuong(voucher.getSoLuong());
            response.setIdKhachHang(voucher.getIdKhachHang());
            response.setNgayKetThuc(voucher.getNgayKetThuc());
            response.setSoTienGiam(tinhTienGiamVoucherKhaDung(voucher, tongTien));
            return response;
        }).sorted((a, b) -> b.getSoTienGiam().compareTo(a.getSoTienGiam())).toList();
        Pageable pageable = PageRequest.of(page, size);
        int start = Math.min((int) pageable.getOffset(), result.size());
        int end = Math.min(start + pageable.getPageSize(), result.size());
        return new PageImpl<>(result.subList(start, end), pageable, result.size());
    }
    private BigDecimal tinhTienGiamVoucherKhaDung(Voucher voucher, BigDecimal tongTien) {
        BigDecimal soTienGiam = BigDecimal.ZERO;
        if ("PERCENT".equals(voucher.getLoaiVoucher())) {
            soTienGiam = tongTien
                    .multiply(voucher.getGiaTriGiam())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (voucher.getGiamToiDa() != null
                    && soTienGiam.compareTo(voucher.getGiamToiDa()) > 0) {
                soTienGiam = voucher.getGiamToiDa();
            }
        } else if ("FIXED".equals(voucher.getLoaiVoucher())) {
            soTienGiam = voucher.getGiaTriGiam();
        }
        return soTienGiam.min(tongTien).setScale(0, RoundingMode.HALF_UP);
    }
    private void kiemTraVoucherThanhToan(HoaDon hoaDon) {
        Voucher voucher = hoaDon.getVoucher();
        if (voucher == null) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        if (voucher.getTrangThai() == null || voucher.getTrangThai() != 1)
            throw new RuntimeException("Voucher không còn khả dụng");
        if (voucher.getSoLuong() != null && voucher.getSoLuong() <= 0)
            throw new RuntimeException("Voucher đã hết lượt sử dụng");
        if (voucher.getNgayBatDau() != null
                && now.isBefore(voucher.getNgayBatDau()))
            throw new RuntimeException("Voucher chưa đến thời gian sử dụng");
        if (voucher.getNgayKetThuc() != null
                && now.isAfter(voucher.getNgayKetThuc()))
            throw new RuntimeException("Voucher đã hết hạn");
        if (voucher.getIdKhachHang() != null
                && (hoaDon.getKhachHang() == null
                || !voucher.getIdKhachHang().equals(
                hoaDon.getKhachHang().getIdKhachHang()))) {
            throw new RuntimeException(
                    "Voucher không thuộc về khách hàng của hóa đơn"
            );
        }
        if (voucher.getDieuKien() != null
                && hoaDon.getTongTien().compareTo(voucher.getDieuKien()) < 0) {
            throw new RuntimeException(
                    "Hóa đơn chưa đạt giá trị tối thiểu để dùng voucher"
            );
        }
    }
    private void kiemTraKhoTruocThanhToan(List<HoaDonChiTiet> chiTietList) {
        Map<Integer, BigDecimal> tongBtpCanDung = new HashMap<>();
        Map<Integer, Double> tongNguyenLieuCanDung = new HashMap<>();
        Map<Integer, Integer> tongToppingCanDung = new HashMap<>();
        for (HoaDonChiTiet chiTiet : chiTietList) {
            SanPham sanPham = sanPhamRepository.findById(chiTiet.getIdSanPham())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            if (sanPham.getTrangThai() == null || sanPham.getTrangThai() != 1) {
                throw new RuntimeException(
                        "Sản phẩm \"" + sanPham.getTenSanPham()
                                + "\" đang ngừng bán. Vui lòng xóa sản phẩm khỏi hóa đơn."
                );
            }
            List<CongThucSanPhamBtp> congThucBtpList =
                    congThucSanPhamBtpRepository.findByIdSanPhamAndIdSize(
                            chiTiet.getIdSanPham(),
                            chiTiet.getIdSize()
                    );
            for (CongThucSanPhamBtp congThuc : congThucBtpList) {
                Integer idBtp = congThuc.getBanThanhPham().getIdBanThanhPham();
                BigDecimal soLuongCan =
                        congThuc.getSoLuongCanDung()
                                .multiply(BigDecimal.valueOf(chiTiet.getSoLuong()));
                tongBtpCanDung.merge(
                        idBtp,
                        soLuongCan,
                        BigDecimal::add); }
            List<CongThucSanPham> congThucNguyenLieuList =
                    congThucSanPhamRepository.findByIdSanPhamAndIdSize(
                            chiTiet.getIdSanPham(),
                            chiTiet.getIdSize()
                    );
            for (CongThucSanPham congThuc : congThucNguyenLieuList) {
                Integer idNguyenLieu =
                        congThuc.getNguyenLieu().getIdNguyenLieu();
                Double soLuongCan =
                        congThuc.getSoLuongCanDung()
                                * chiTiet.getSoLuong();
                tongNguyenLieuCanDung.merge(
                        idNguyenLieu,
                        soLuongCan,
                        Double::sum
                );
            }
            List<HdctTopping> toppingList =
                    hdctToppingRepository
                            .findByHoaDonChiTiet_IdHoaDonChiTiet(
                                    chiTiet.getIdHoaDonChiTiet()
                            );
            for (HdctTopping topping : toppingList) {
                tongToppingCanDung.merge(
                        topping.getIdTopping(),
                        topping.getSoLuong(),
                        Integer::sum
                );
            }
        }
        for (Map.Entry<Integer, BigDecimal> entry : tongBtpCanDung.entrySet()) {
            banThanhPhamKhoService.kiemTraDuTon(
                    entry.getKey(),
                    entry.getValue()
            );
        }
        for (Map.Entry<Integer, Double> entry : tongNguyenLieuCanDung.entrySet()) {
            khoService.kiemTraDuTon(
                    entry.getKey(),
                    entry.getValue()
            );
        }
        for (Map.Entry<Integer, Integer> entry : tongToppingCanDung.entrySet()) {
            toppingKhoService.kiemTraDuTon(
                    entry.getKey(),
                    entry.getValue()
            );
        }
    }
    @Transactional
    public PayOSCreateResponse taoThanhToanPayOS(Integer idHoaDon) {
        HoaDon hoaDon = getHoaDonChoThanhToanDeSua(idHoaDon);
        List<HoaDonChiTiet> chiTietList =
                chiTietRepository.findByHoaDon_IdHoaDon(idHoaDon);
        if (chiTietList.isEmpty()) {
            throw new RuntimeException("Hóa đơn chưa có sản phẩm");
        }
        if (hoaDon.getThanhTien() == null
                || hoaDon.getThanhTien().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Số tiền thanh toán không hợp lệ");
        }
        // QUAN TRỌNG: kiểm tra toàn bộ kho trước khi gọi PayOS
        kiemTraKhoTruocThanhToan(chiTietList);
        kiemTraVoucherThanhToan(hoaDon);
        Long amount = hoaDon.getThanhTien().longValueExact();
        String description = "KCDrink " + hoaDon.getMaHoaDon();
        PayOSCreateResponse payment =
                payOSService.createPayment(amount, description);
        hoaDon.setPayosOrderCode(payment.getOrderCode());
        hoaDon.setPayosPaymentLinkId(payment.getPaymentLinkId());
        hoaDon.setPayosStatus(payment.getStatus());
        repository.save(hoaDon);
        return payment;
    }
    @Transactional
    public PayOSPaymentStatusResponse layTrangThaiPayOS(Integer idHoaDon) {
        HoaDon hoaDon = repository.findByIdForUpdate(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        if (hoaDon.getPayosOrderCode() == null) {
            throw new RuntimeException("Hóa đơn chưa có giao dịch PayOS");
        }
        if (hoaDon.getThanhTien() == null
                || hoaDon.getThanhTien().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Số tiền thanh toán không hợp lệ");
        }
        PayOSPaymentStatusResponse paymentInfo =
                payOSService.getPaymentStatusInfo(hoaDon.getPayosOrderCode());
        Long expectedAmount = hoaDon.getThanhTien().longValueExact();
        if (!expectedAmount.equals(paymentInfo.getAmount())) {
            throw new RuntimeException("Số tiền giao dịch PayOS không khớp với hóa đơn");
        }
        hoaDon.setPayosStatus(paymentInfo.getStatus());
        repository.save(hoaDon);
        return paymentInfo;
    }
    @Transactional
    public PayOSPaymentStatusResponse huyThanhToanPayOS(Integer idHoaDon) {
        HoaDon hoaDon = getHoaDonChoThanhToanCoKhoa(idHoaDon);
        if (hoaDon.getPayosOrderCode() == null) {
            throw new RuntimeException("Hóa đơn chưa có giao dịch PayOS");
        }
        if (hoaDon.getThanhTien() == null
                || hoaDon.getThanhTien().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Số tiền thanh toán không hợp lệ");
        }
        Long expectedAmount = hoaDon.getThanhTien().longValueExact();
        PayOSPaymentStatusResponse paymentInfo =
                payOSService.getPaymentStatusInfo(hoaDon.getPayosOrderCode());
        if (!expectedAmount.equals(paymentInfo.getAmount())) {
            throw new RuntimeException("Số tiền giao dịch PayOS không khớp với hóa đơn");
        }
        String status = paymentInfo.getStatus();
        if ("PAID".equals(status)) {
            throw new RuntimeException("Giao dịch đã được thanh toán, không thể hủy");
        }
        if ("CANCELLED".equals(status) || "EXPIRED".equals(status)) {
            hoaDon.setPayosStatus(status);
            repository.save(hoaDon);
            return paymentInfo;
        }
        if (!"PENDING".equals(status)) {
            throw new RuntimeException("Trạng thái giao dịch PayOS không cho phép hủy");
        }
        PayOSPaymentStatusResponse cancelledPayment =
                payOSService.cancelPayment(
                        hoaDon.getPayosOrderCode(),
                        "Nhan vien huy thanh toan tai POS"
                );
        if (!expectedAmount.equals(cancelledPayment.getAmount())) {
            throw new RuntimeException("Số tiền giao dịch PayOS không khớp với hóa đơn");
        }
        if (!"CANCELLED".equals(cancelledPayment.getStatus())) {
            throw new RuntimeException("PayOS chưa xác nhận hủy giao dịch");
        }
        hoaDon.setPayosStatus(cancelledPayment.getStatus());
        repository.save(hoaDon);
        return cancelledPayment;
    }
    private boolean payOSDangKhoaHoaDon(HoaDon hoaDon) {
        String status = hoaDon.getPayosStatus();
        return status != null
                && !status.isBlank()
                && !"CANCELLED".equalsIgnoreCase(status)
                && !"EXPIRED".equalsIgnoreCase(status);
    }

    private HoaDonListResponse toListResponse(
            HoaDon hoaDon,
            VanDonGhn vanDon
    ) {
        KhachHang khachHang = hoaDon.getKhachHang();
        NhanVien nhanVien = hoaDon.getNhanVien();

        return HoaDonListResponse.builder()
                .idHoaDon(hoaDon.getIdHoaDon())
                .maHoaDon(hoaDon.getMaHoaDon())
                .loaiHoaDon(hoaDon.getLoaiHoaDon())
                .ngayTao(hoaDon.getNgayTao())
                .tongTien(hoaDon.getTongTien())
                .giamGia(hoaDon.getGiamGia())
                .phiVanChuyen(hoaDon.getPhiVanChuyen())
                .thanhTien(hoaDon.getThanhTien())
                .hinhThucThanhToan(hoaDon.getHinhThucThanhToan())
                .payosStatus(hoaDon.getPayosStatus())
                .trangThai(hoaDon.getTrangThai())
                .idKhachHang(khachHang == null ? null : khachHang.getIdKhachHang())
                .tenKhachHang(khachHang == null ? null : khachHang.getTenKhachHang())
                .sdtKhachHang(khachHang == null ? null : khachHang.getSdt())
                .idNhanVien(nhanVien == null ? null : nhanVien.getIdNhanVien())
                .tenNhanVien(nhanVien == null ? null : nhanVien.getTenNhanVien())
                .coGiaoHang(vanDon != null)
                .maVanDonGhn(vanDon == null ? null : vanDon.getMaVanDonGhn())
                .trangThaiVanDon(vanDon == null ? null : vanDon.getTrangThai())
                .trangThaiGhn(vanDon == null ? null : vanDon.getTrangThaiGhn())
                .thoiGianGiaoDuKien(vanDon == null ? null : vanDon.getThoiGianGiaoDuKien())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<HoaDonListResponse> getDanhSachHoaDon(
            String keyword,
            String trangThai,
            String loaiHoaDon,
            String hinhThucThanhToan,
            Boolean coGiaoHang,
            String trangThaiGhn,
            LocalDate tuNgay,
            LocalDate denNgay,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        Set<String> allowedSortFields = Set.of(
                "idHoaDon",
                "maHoaDon",
                "ngayTao",
                "tongTien",
                "giamGia",
                "phiVanChuyen",
                "thanhTien",
                "loaiHoaDon",
                "trangThai",
                "hinhThucThanhToan"
        );

        String safeSortBy = allowedSortFields.contains(sortBy)
                ? sortBy
                : "ngayTao";

        Sort.Direction sortDirection =
                "asc".equalsIgnoreCase(direction)
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(sortDirection, safeSortBy)
        );

        String normalizedKeyword = keyword == null
                ? null
                : keyword.trim();

        LocalDateTime tuNgayTime = tuNgay == null
                ? null
                : tuNgay.atStartOfDay();

        LocalDateTime denNgayExclusive = denNgay == null
                ? null
                : denNgay.plusDays(1).atStartOfDay();

        Page<HoaDon> hoaDonPage = repository.searchHoaDon(
                normalizedKeyword,
                trangThai,
                loaiHoaDon,
                hinhThucThanhToan,
                coGiaoHang,
                trangThaiGhn,
                tuNgayTime,
                denNgayExclusive,
                pageable
        );

        List<Integer> idHoaDonList = hoaDonPage.getContent()
                .stream()
                .map(HoaDon::getIdHoaDon)
                .toList();

        Map<Integer, VanDonGhn> vanDonMap;

        if (idHoaDonList.isEmpty()) {
            vanDonMap = Map.of();
        } else {
            vanDonMap = vanDonGhnRepository
                    .findAllByHoaDon_IdHoaDonIn(idHoaDonList)
                    .stream()
                    .collect(Collectors.toMap(
                            vanDon -> vanDon.getHoaDon().getIdHoaDon(),
                            Function.identity(),
                            (oldValue, newValue) -> oldValue
                    ));
        }

        List<HoaDonListResponse> content = hoaDonPage
                .getContent()
                .stream()
                .map(hoaDon -> toListResponse(
                        hoaDon,
                        vanDonMap.get(hoaDon.getIdHoaDon())
                ))
                .toList();

        return new PageResponse<>(
                content,
                hoaDonPage.getNumber(),
                hoaDonPage.getSize(),
                hoaDonPage.getTotalElements(),
                hoaDonPage.getTotalPages(),
                hoaDonPage.isLast()
        );
    }
}