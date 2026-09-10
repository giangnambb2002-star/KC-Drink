package com.example.datn.dashboard.service;

import com.example.datn.dashboard.dto.DashboardNgayResponse;
import com.example.datn.dashboard.dto.DashboardResponse;
import com.example.datn.khach_hang.repository.KhachHangRepository;
import com.example.datn.nguyen_lieu.repository.LoNguyenLieuRepository;
import com.example.datn.nguyen_lieu.repository.NguyenLieuRepository;
import com.example.datn.nhan_vien.repository.NhanVienRepository;
import com.example.datn.topping.repository.LoToppingRepository;
import com.example.datn.topping.repository.ToppingRepository;
import com.example.datn.hoa_don.repository.HoaDonRepository;
import com.example.datn.van_chuyen.repository.VanDonGhnRepository;
import com.example.datn.dashboard.dto.DashboardNhanVienResponse;
import com.example.datn.dashboard.dto.DashboardSanPhamBanChayResponse;
import com.example.datn.hoa_don.repository.HoaDonChiTietRepository;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.datn.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final VoucherRepository voucherRepository;
    private final ToppingRepository toppingRepository;
    private final NguyenLieuRepository nguyenLieuRepository;
    private final LoToppingRepository loToppingRepository;
    private final LoNguyenLieuRepository loNguyenLieuRepository;
    private final HoaDonRepository hoaDonRepository;
    private final VanDonGhnRepository vanDonGhnRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;

    public DashboardResponse getDashboard() {

        LocalDate homNay = LocalDate.now();
        LocalDate bayNgayToi = homNay.plusDays(7);

        LocalDateTime dauNgay = homNay.atStartOfDay();
        LocalDateTime dauNgayMai = homNay.plusDays(1).atStartOfDay();

        LocalDateTime dau7Ngay =
                homNay.minusDays(6).atStartOfDay();

        List<DashboardSanPhamBanChayResponse> topSanPham7Ngay =
                hoaDonChiTietRepository.findSanPhamBanChay(
                        dau7Ngay,
                        dauNgayMai,
                        PageRequest.of(0, 5)
                );

        BigDecimal doanhThuHomNay =
                hoaDonRepository.sumDoanhThuDaThanhToan(
                        dauNgay,
                        dauNgayMai
                );

        long donOnlineHomNay =
                hoaDonRepository
                        .countByLoaiHoaDonAndNgayTaoGreaterThanEqualAndNgayTaoLessThan(
                                "ONLINE",
                                dauNgay,
                                dauNgayMai
                        );

        long donOfflineHomNay =
                hoaDonRepository
                        .countByLoaiHoaDonAndNgayTaoGreaterThanEqualAndNgayTaoLessThan(
                                "OFFLINE",
                                dauNgay,
                                dauNgayMai
                        );

        long donDaThanhToanHomNay =
                hoaDonRepository
                        .countByTrangThaiAndNgayTaoGreaterThanEqualAndNgayTaoLessThan(
                                "DA_THANH_TOAN",
                                dauNgay,
                                dauNgayMai
                        );

        long donDaHuyHomNay =
                hoaDonRepository
                        .countByTrangThaiAndNgayTaoGreaterThanEqualAndNgayTaoLessThan(
                                "DA_HUY",
                                dauNgay,
                                dauNgayMai
                        );

        long donChoTiepNhan =
                vanDonGhnRepository
                        .countDonOnlineTheoTrangThai("CHO_TAO_DON");

        long donDaTiepNhan =
                vanDonGhnRepository
                        .countDonOnlineTheoTrangThai("DA_TIEP_NHAN");

        long donDangGiao =
                vanDonGhnRepository
                        .countDonOnlineTheoTrangThai("DANG_GIAO");

        long donDaGiao =
                vanDonGhnRepository
                        .countDonOnlineTheoTrangThai("GIAO_THANH_CONG");


        List<DashboardNhanVienResponse> thongKeNhanVien7Ngay =
                hoaDonRepository.findThongKeNhanVien(
                        dau7Ngay,
                        dauNgayMai
                );
        // ===== BIỂU ĐỒ 7 NGÀY =====

        List<DashboardNgayResponse> bieuDo7Ngay = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {

            LocalDate ngay = homNay.minusDays(i);

            LocalDateTime batDauNgay =
                    ngay.atStartOfDay();

            LocalDateTime ketThucNgay =
                    ngay.plusDays(1).atStartOfDay();

            BigDecimal doanhThuNgay =
                    hoaDonRepository.sumDoanhThuDaThanhToan(
                            batDauNgay,
                            ketThucNgay
                    );

            long donOnlineNgay =
                    hoaDonRepository
                            .countByLoaiHoaDonAndNgayTaoGreaterThanEqualAndNgayTaoLessThan(
                                    "ONLINE",
                                    batDauNgay,
                                    ketThucNgay
                            );

            long donOfflineNgay =
                    hoaDonRepository
                            .countByLoaiHoaDonAndNgayTaoGreaterThanEqualAndNgayTaoLessThan(
                                    "OFFLINE",
                                    batDauNgay,
                                    ketThucNgay
                            );

            bieuDo7Ngay.add(
                    new DashboardNgayResponse(
                            ngay,
                            doanhThuNgay,
                            donOnlineNgay,
                            donOfflineNgay
                    )
            );
        }


        return new DashboardResponse(
                khachHangRepository.countByTrangThai(1),
                nhanVienRepository.countByTrangThai(1),
                voucherRepository.countVoucherDangHoatDong(),
                toppingRepository.countByTrangThai(1),
                nguyenLieuRepository.countByTrangThai(1),

                loToppingRepository.countSapHetHan(
                        homNay,
                        bayNgayToi
                ),
                loNguyenLieuRepository.countSapHetHan(
                        homNay,
                        bayNgayToi
                ),
                nguyenLieuRepository.countNguyenLieuDuoiNguong(),

                doanhThuHomNay,
                donOnlineHomNay,
                donOfflineHomNay,
                donDaThanhToanHomNay,
                donDaHuyHomNay,

                donChoTiepNhan,
                donDaTiepNhan,
                donDangGiao,
                donDaGiao,

                bieuDo7Ngay,
                topSanPham7Ngay,
                thongKeNhanVien7Ngay
        );
    }

}