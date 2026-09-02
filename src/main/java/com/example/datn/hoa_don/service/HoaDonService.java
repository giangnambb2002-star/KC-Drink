package com.example.datn.hoa_don.service;

import com.example.datn.ban_thanh_pham.entity.CongThucSanPhamBtp;
import com.example.datn.ban_thanh_pham.repository.CongThucSanPhamBtpRepository;
import com.example.datn.ban_thanh_pham.service.BanThanhPhamKhoService;
import com.example.datn.hoa_don.dto.ApDungVoucherRequest;
import com.example.datn.hoa_don.dto.CapNhatKhachHangHoaDonRequest;
import com.example.datn.hoa_don.dto.CapNhatSoLuongRequest;
import com.example.datn.hoa_don.dto.CapNhatToppingRequest;
import com.example.datn.hoa_don.dto.HdctToppingResponse;
import com.example.datn.hoa_don.dto.HoaDonChiTietResponse;
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
import com.example.datn.san_pham.entity.SanPhamSize;
import com.example.datn.san_pham.repository.SanPhamRepository;
import com.example.datn.topping.repository.LoToppingRepository;
import com.example.datn.topping.service.ToppingKhoService;
import com.example.datn.san_pham.entity.SanPham;
import com.example.datn.san_pham.entity.Size;
import com.example.datn.san_pham.repository.SanPhamSizeRepository;
import com.example.datn.san_pham.repository.SizeRepository;
import com.example.datn.topping.entity.Topping;
import com.example.datn.topping.repository.ToppingRepository;
import com.example.datn.voucher.entity.Voucher;
import com.example.datn.voucher.repository.VoucherRepository;
import com.example.datn.hoa_don.dto.VoucherKhaDungResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
        HoaDon hoaDon = getHoaDonChoThanhToan(idHoaDon);

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

        HoaDon hoaDon = getHoaDonChoThanhToan(
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
        HoaDon hoaDon = getHoaDonChoThanhToan(
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
        HoaDon hoaDon = repository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

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

        hoaDon.setTongTien(tongTien);
        BigDecimal giamGia = BigDecimal.ZERO;
        Voucher voucher = hoaDon.getVoucher();
        if (voucher != null) {
            if (tongTien.compareTo(BigDecimal.ZERO) <= 0) {
                hoaDon.setVoucher(null);
            } else if (voucher.getDieuKien() != null && tongTien.compareTo(voucher.getDieuKien()) < 0) {
                hoaDon.setVoucher(null);
            } else {
                giamGia = tinhTienGiamVoucher(voucher, tongTien);
            }
        }
        hoaDon.setGiamGia(giamGia);
        BigDecimal thanhTien = tongTien.subtract(giamGia);
        hoaDon.setThanhTien(thanhTien.max(BigDecimal.ZERO));
        repository.save(hoaDon);
    }

    private BigDecimal tinhTienGiamVoucher(Voucher voucher, BigDecimal tongTien) {
        BigDecimal giamGia = BigDecimal.ZERO;

        if ("PERCENT".equals(voucher.getLoaiVoucher())) {
            giamGia = tongTien
                    .multiply(voucher.getGiaTriGiam())
                    .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);

            if (voucher.getGiamToiDa() != null && giamGia.compareTo(voucher.getGiamToiDa()) > 0)
                giamGia = voucher.getGiamToiDa();
        } else if ("FIXED".equals(voucher.getLoaiVoucher())) {
            giamGia = voucher.getGiaTriGiam();
        }

        return giamGia.min(tongTien);
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
        response.setHinhThucThanhToan(
                hoaDon.getHinhThucThanhToan()
        );
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
        HoaDon hoaDon = getHoaDonChoThanhToan(idHoaDon);
        List<HoaDonChiTiet> chiTietList = chiTietRepository.findByHoaDon_IdHoaDon(idHoaDon);

        if (chiTietList.isEmpty()) throw new RuntimeException("Hóa đơn chưa có sản phẩm");
        if (request.getHinhThucThanhToan() == null || request.getHinhThucThanhToan().isBlank())
            throw new RuntimeException("Vui lòng chọn hình thức thanh toán");
        if (!"TIEN_MAT".equals(request.getHinhThucThanhToan()))
            throw new RuntimeException("Hình thức thanh toán chưa được hỗ trợ");
        for (HoaDonChiTiet chiTiet : chiTietList) {
            SanPham sanPham = sanPhamRepository.findById(chiTiet.getIdSanPham())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            if (sanPham.getTrangThai() == null || sanPham.getTrangThai() != 1) {
                throw new RuntimeException(
                        "Sản phẩm \"" + sanPham.getTenSanPham()
                                + "\" đang ngừng bán. Vui lòng xóa sản phẩm khỏi hóa đơn."
                );
            }
        }
        Voucher voucher = hoaDon.getVoucher();
        if (voucher != null) {
            LocalDateTime now = LocalDateTime.now();
            if (voucher.getTrangThai() == null || voucher.getTrangThai() != 1)
                throw new RuntimeException("Voucher không còn khả dụng");
            if (voucher.getSoLuong() != null && voucher.getSoLuong() <= 0)
                throw new RuntimeException("Voucher đã hết lượt sử dụng");
            if (voucher.getNgayBatDau() != null && now.isBefore(voucher.getNgayBatDau()))
                throw new RuntimeException("Voucher chưa đến thời gian sử dụng");
            if (voucher.getNgayKetThuc() != null && now.isAfter(voucher.getNgayKetThuc()))
                throw new RuntimeException("Voucher đã hết hạn");
            if (voucher.getIdKhachHang() != null &&
                    (hoaDon.getKhachHang() == null ||
                            !voucher.getIdKhachHang().equals(hoaDon.getKhachHang().getIdKhachHang())))
                throw new RuntimeException("Voucher không thuộc về khách hàng của hóa đơn");
            if (voucher.getDieuKien() != null && hoaDon.getTongTien().compareTo(voucher.getDieuKien()) < 0)
                throw new RuntimeException("Hóa đơn chưa đạt giá trị tối thiểu để dùng voucher");
        }

        for (HoaDonChiTiet chiTiet : chiTietList) {
            List<CongThucSanPhamBtp> congThucBtpList =
                    congThucSanPhamBtpRepository.findByIdSanPhamAndIdSize(
                            chiTiet.getIdSanPham(), chiTiet.getIdSize());
            for (CongThucSanPhamBtp congThuc : congThucBtpList) {
                BigDecimal soLuongCanTru = congThuc.getSoLuongCanDung()
                        .multiply(BigDecimal.valueOf(chiTiet.getSoLuong()));
                banThanhPhamKhoService.truBanThanhPham(
                        congThuc.getBanThanhPham().getIdBanThanhPham(), soLuongCanTru);
            }

            List<CongThucSanPham> congThucNguyenLieuList =
                    congThucSanPhamRepository.findByIdSanPhamAndIdSize(
                            chiTiet.getIdSanPham(), chiTiet.getIdSize());

            for (CongThucSanPham congThuc : congThucNguyenLieuList) {
                Double soLuongCanTru = congThuc.getSoLuongCanDung() * chiTiet.getSoLuong();
                khoService.truKhoNguyenLieu(
                        congThuc.getNguyenLieu().getIdNguyenLieu(), soLuongCanTru);
            }

            List<HdctTopping> toppingList =
                    hdctToppingRepository.findByHoaDonChiTiet_IdHoaDonChiTiet(
                            chiTiet.getIdHoaDonChiTiet());

            for (HdctTopping topping : toppingList) {
                toppingKhoService.truKhoTopping(
                        topping.getIdTopping(), topping.getSoLuong());
            }
        }

        if (voucher != null && voucher.getSoLuong() != null) {
            int soLuongConLai = voucher.getSoLuong() - 1;
            voucher.setSoLuong(Math.max(soLuongConLai, 0));
            if (soLuongConLai <= 0) voucher.setTrangThai(0);
            voucherRepository.save(voucher);
        }

        hoaDon.setHinhThucThanhToan("TIEN_MAT");
        hoaDon.setTrangThai("DA_THANH_TOAN");
        return toResponse(repository.save(hoaDon));
    }
    @Transactional
    public HoaDonResponse themTopping(Integer idChiTiet, ThemToppingRequest request) {
        HoaDonChiTiet chiTiet = chiTietRepository.findById(idChiTiet)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết hóa đơn"));

        HoaDon hoaDon = getHoaDonChoThanhToan(chiTiet.getHoaDon().getIdHoaDon());

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

        HoaDon hoaDon = getHoaDonChoThanhToan(
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

        HoaDon hoaDon = getHoaDonChoThanhToan(
                topping.getHoaDonChiTiet().getHoaDon().getIdHoaDon()
        );
        hdctToppingRepository.delete(topping);
        hdctToppingRepository.flush();
        tinhLaiTongTien(hoaDon);
        return toResponse(hoaDon);
    }

    @Transactional
    public HoaDonResponse huyHoaDon(Integer idHoaDon) {
        HoaDon hoaDon = getHoaDonChoThanhToan(idHoaDon);

        hoaDon.setTrangThai("DA_HUY");

        return toResponse(repository.save(hoaDon));
    }
    @Transactional
    public HoaDonResponse capNhatKhachHang(
            Integer idHoaDon,
            CapNhatKhachHangHoaDonRequest request
    ) {
        HoaDon hoaDon = getHoaDonChoThanhToan(idHoaDon);

        if (request.getIdKhachHang() == null) {
            hoaDon.setKhachHang(null);
            return toResponse(repository.save(hoaDon));
        }

        KhachHang khachHang = khachHangRepository.findById(request.getIdKhachHang())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        if (khachHang.getTrangThai() == null || khachHang.getTrangThai() != 1) {
            throw new RuntimeException("Khách hàng đang bị khóa");
        }

        hoaDon.setKhachHang(khachHang);

        return toResponse(repository.save(hoaDon));
    }

    @Transactional
    public HoaDonResponse apDungVoucher(Integer idHoaDon, ApDungVoucherRequest request) {
        HoaDon hoaDon = getHoaDonChoThanhToan(idHoaDon);

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
                    .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);

            if (voucher.getGiamToiDa() != null &&
                    soTienGiam.compareTo(voucher.getGiamToiDa()) > 0) {
                soTienGiam = voucher.getGiamToiDa();
            }
        } else if ("FIXED".equals(voucher.getLoaiVoucher())) {
            soTienGiam = voucher.getGiaTriGiam();
        } else {
            throw new RuntimeException("Loại voucher không hợp lệ");
        }

        if (soTienGiam.compareTo(tongTien) > 0) {
            soTienGiam = tongTien;
        }

        hoaDon.setVoucher(voucher);
        hoaDon.setGiamGia(soTienGiam);
        hoaDon.setThanhTien(tongTien.subtract(soTienGiam));

        return toResponse(repository.save(hoaDon));
    }
    @Transactional
    public HoaDonResponse boVoucher(Integer idHoaDon) {
        HoaDon hoaDon = getHoaDonChoThanhToan(idHoaDon);
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
            soTienGiam = tongTien.multiply(voucher.getGiaTriGiam())
                    .divide(BigDecimal.valueOf(100));
            if (voucher.getGiamToiDa() != null && soTienGiam.compareTo(voucher.getGiamToiDa()) > 0) {
                soTienGiam = voucher.getGiamToiDa();
            }
        } else if ("FIXED".equals(voucher.getLoaiVoucher())) {
            soTienGiam = voucher.getGiaTriGiam();
        }
        return soTienGiam.compareTo(tongTien) > 0 ? tongTien : soTienGiam;
    }
}