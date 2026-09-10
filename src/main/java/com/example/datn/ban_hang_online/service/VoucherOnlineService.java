package com.example.datn.ban_hang_online.service;

import com.example.datn.ban_hang_online.dto.checkout.VoucherOnlineResponse;
import com.example.datn.khach_hang.entity.KhachHang;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import com.example.datn.voucher.entity.Voucher;
import com.example.datn.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherOnlineService {

    private final VoucherRepository voucherRepository;

    private final KhachHangOnlineService khachHangOnlineService;

    public List<VoucherOnlineResponse> getVoucherKhaDung(
            BigDecimal tongTien,
            TaiKhoan taiKhoan
    ) {

        if (tongTien == null
                || tongTien.compareTo(BigDecimal.ZERO) <= 0) {
            return List.of();
        }

        /*
         * Không lấy idKhachHang từ FE.
         * Luôn xác định khách từ tài khoản đang đăng nhập.
         */
        KhachHang khachHang =
                khachHangOnlineService
                        .getCurrentCustomer(taiKhoan);

        Integer idKhachHang =
                khachHang.getIdKhachHang();

        /*
         * Query dùng chung của hệ thống.
         *
         * Nó lấy:
         * - voucher hệ thống: idKhachHang = null
         * - voucher riêng đúng khách hiện tại
         * - còn hoạt động
         * - còn lượt
         * - đúng thời gian
         * - đủ điều kiện đơn
         */
        return voucherRepository
                .findVoucherKhaDung(
                        tongTien,
                        idKhachHang,
                        LocalDateTime.now()
                )
                .stream()
                .map(voucher ->
                        toResponse(
                                voucher,
                                tongTien
                        )
                )
                /*
                 * Voucher giảm được nhiều tiền hơn
                 * đưa lên đầu danh sách.
                 */
                .sorted(
                        (a, b) ->
                                b.getSoTienGiam()
                                        .compareTo(
                                                a.getSoTienGiam()
                                        )
                )
                .toList();
    }

    private VoucherOnlineResponse toResponse(
            Voucher voucher,
            BigDecimal tongTien
    ) {

        VoucherOnlineResponse response =
                new VoucherOnlineResponse();

        response.setIdVoucher(
                voucher.getIdVoucher()
        );

        response.setMaVoucher(
                voucher.getMaVoucher()
        );

        response.setTenVoucher(
                voucher.getTenVoucher()
        );

        response.setLoaiVoucher(
                voucher.getLoaiVoucher()
        );

        response.setGiaTriGiam(
                voucher.getGiaTriGiam()
        );

        response.setGiamToiDa(
                voucher.getGiamToiDa()
        );

        response.setDieuKien(
                voucher.getDieuKien()
        );

        response.setSoLuong(
                voucher.getSoLuong()
        );

        response.setIdKhachHang(
                voucher.getIdKhachHang()
        );

        response.setNgayBatDau(
                voucher.getNgayBatDau()
        );

        response.setNgayKetThuc(
                voucher.getNgayKetThuc()
        );

        response.setSoTienGiam(
                tinhTienGiam(
                        voucher,
                        tongTien
                )
        );

        return response;
    }

    private BigDecimal tinhTienGiam(
            Voucher voucher,
            BigDecimal tongTien
    ) {

        BigDecimal soTienGiam =
                BigDecimal.ZERO;

        if ("PERCENT".equals(
                voucher.getLoaiVoucher()
        )) {

            soTienGiam =
                    tongTien
                            .multiply(
                                    voucher.getGiaTriGiam()
                            )
                            .divide(
                                    BigDecimal.valueOf(100),
                                    2,
                                    RoundingMode.HALF_UP
                            );

            if (voucher.getGiamToiDa() != null
                    && soTienGiam.compareTo(
                    voucher.getGiamToiDa()
            ) > 0) {

                soTienGiam =
                        voucher.getGiamToiDa();
            }

        } else if ("FIXED".equals(
                voucher.getLoaiVoucher()
        )) {

            soTienGiam =
                    voucher.getGiaTriGiam();
        }

        return soTienGiam
                .min(tongTien)
                .setScale(
                        0,
                        RoundingMode.HALF_UP
                );
    }
}