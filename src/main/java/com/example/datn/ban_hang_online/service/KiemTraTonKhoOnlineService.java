package com.example.datn.ban_hang_online.service;

import com.example.datn.ban_hang_online.dto.checkout.ChiTietGioHangRequest;
import com.example.datn.ban_hang_online.dto.checkout.ToppingGioHangRequest;
import com.example.datn.ban_hang_online.dto.checkout.XemTruocGioHangRequest;
import com.example.datn.ban_hang_online.repository.DatChoKhoRepository;
import com.example.datn.ban_thanh_pham.entity.CongThucSanPhamBtp;
import com.example.datn.ban_thanh_pham.repository.CongThucSanPhamBtpRepository;
import com.example.datn.ban_thanh_pham.service.BanThanhPhamKhoService;
import com.example.datn.nguyen_lieu.entity.CongThucSanPham;
import com.example.datn.nguyen_lieu.repository.CongThucSanPhamRepository;
import com.example.datn.nguyen_lieu.service.KhoService;
import com.example.datn.topping.service.ToppingKhoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KiemTraTonKhoOnlineService {

    private final CongThucSanPhamBtpRepository congThucSanPhamBtpRepository;
    private final BanThanhPhamKhoService banThanhPhamKhoService;

    private final CongThucSanPhamRepository congThucSanPhamRepository;
    private final KhoService khoService;

    private final ToppingKhoService toppingKhoService;

    private final DatChoKhoRepository datChoKhoRepository;

    /**
     * Chỉ KIỂM TRA tồn kho.
     *
     * Không trừ kho.
     * Không tạo reservation.
     *
     * Tồn khả dụng ONLINE:
     *
     * tồn vật lý
     * -
     * DAT_CHO_KHO ACTIVE chưa hết hạn.
     */
    public void kiemTraDuTon(
            XemTruocGioHangRequest request
    ) {

        Map<Integer, BigDecimal> tongBtpCanDung =
                new HashMap<>();

        Map<Integer, Double> tongNguyenLieuCanDung =
                new HashMap<>();

        Map<Integer, Integer> tongToppingCanDung =
                new HashMap<>();

        // Dùng cùng một thời điểm trong toàn bộ lần kiểm tra.
        LocalDateTime now = LocalDateTime.now();

        for (ChiTietGioHangRequest item
                : request.getItems()) {

            // =========================
            // BÁN THÀNH PHẨM
            // =========================
            List<CongThucSanPhamBtp> congThucBtpList =
                    congThucSanPhamBtpRepository
                            .findByIdSanPhamAndIdSize(
                                    item.getIdSanPham(),
                                    item.getIdSize()
                            );

            for (CongThucSanPhamBtp congThuc
                    : congThucBtpList) {

                Integer idBanThanhPham =
                        congThuc
                                .getBanThanhPham()
                                .getIdBanThanhPham();

                BigDecimal soLuongCan =
                        congThuc
                                .getSoLuongCanDung()
                                .multiply(
                                        BigDecimal.valueOf(
                                                item.getSoLuong()
                                        )
                                );

                tongBtpCanDung.merge(
                        idBanThanhPham,
                        soLuongCan,
                        BigDecimal::add
                );
            }

            // =========================
            // NGUYÊN LIỆU TRỰC TIẾP
            // =========================
            List<CongThucSanPham> congThucNguyenLieuList =
                    congThucSanPhamRepository
                            .findByIdSanPhamAndIdSize(
                                    item.getIdSanPham(),
                                    item.getIdSize()
                            );

            for (CongThucSanPham congThuc
                    : congThucNguyenLieuList) {

                Integer idNguyenLieu =
                        congThuc
                                .getNguyenLieu()
                                .getIdNguyenLieu();

                Double soLuongCan =
                        congThuc.getSoLuongCanDung()
                                * item.getSoLuong();

                tongNguyenLieuCanDung.merge(
                        idNguyenLieu,
                        soLuongCan,
                        Double::sum
                );
            }

            // =========================
            // TOPPING
            // =========================
            if (item.getToppings() != null) {

                for (ToppingGioHangRequest topping
                        : item.getToppings()) {

                    /*
                     * QUAN TRỌNG:
                     *
                     * topping x1 = 1 phần tổng của dòng.
                     *
                     * KHÔNG nhân thêm với số lượng ly.
                     */
                    tongToppingCanDung.merge(
                            topping.getIdTopping(),
                            topping.getSoLuong(),
                            Integer::sum
                    );
                }
            }
        }

        // =========================
        // KIỂM TRA BTP
        // =========================
        for (Map.Entry<Integer, BigDecimal> entry
                : tongBtpCanDung.entrySet()) {

            Integer idBanThanhPham = entry.getKey();
            BigDecimal soLuongCan = entry.getValue();

            // Giữ validation cũ:
            // tồn tại / trạng thái / tồn vật lý.
            banThanhPhamKhoService.kiemTraDuTon(
                    idBanThanhPham,
                    soLuongCan
            );

            BigDecimal tonVatLy =
                    banThanhPhamKhoService
                            .getTongTonKhaDung(
                                    idBanThanhPham
                            );

            BigDecimal dangGiu =
                    getSoLuongDangGiu(
                            "BAN_THANH_PHAM",
                            idBanThanhPham,
                            now
                    );

            BigDecimal tonKhaDung =
                    tinhTonKhaDung(
                            tonVatLy,
                            dangGiu
                    );

            kiemTraTonSauDatCho(
                    soLuongCan,
                    tonKhaDung
            );
        }

        // =========================
        // KIỂM TRA NGUYÊN LIỆU
        // =========================
        for (Map.Entry<Integer, Double> entry
                : tongNguyenLieuCanDung.entrySet()) {

            Integer idNguyenLieu = entry.getKey();
            Double soLuongCanDouble = entry.getValue();

            // Giữ validation cũ.
            khoService.kiemTraDuTon(
                    idNguyenLieu,
                    soLuongCanDouble
            );

            BigDecimal soLuongCan =
                    BigDecimal.valueOf(
                            soLuongCanDouble
                    );

            BigDecimal tonVatLy =
                    khoService.getTongTonKhaDung(
                            idNguyenLieu
                    );

            BigDecimal dangGiu =
                    getSoLuongDangGiu(
                            "NGUYEN_LIEU",
                            idNguyenLieu,
                            now
                    );

            BigDecimal tonKhaDung =
                    tinhTonKhaDung(
                            tonVatLy,
                            dangGiu
                    );

            kiemTraTonSauDatCho(
                    soLuongCan,
                    tonKhaDung
            );
        }

        // =========================
        // KIỂM TRA TOPPING
        // =========================
        for (Map.Entry<Integer, Integer> entry
                : tongToppingCanDung.entrySet()) {

            Integer idTopping = entry.getKey();
            Integer soLuongCanInteger = entry.getValue();

            // Giữ validation cũ.
            toppingKhoService.kiemTraDuTon(
                    idTopping,
                    soLuongCanInteger
            );

            BigDecimal soLuongCan =
                    BigDecimal.valueOf(
                            soLuongCanInteger
                    );

            BigDecimal tonVatLy =
                    toppingKhoService
                            .getTongTonKhaDung(
                                    idTopping
                            );

            BigDecimal dangGiu =
                    getSoLuongDangGiu(
                            "TOPPING",
                            idTopping,
                            now
                    );

            BigDecimal tonKhaDung =
                    tinhTonKhaDung(
                            tonVatLy,
                            dangGiu
                    );

            kiemTraTonSauDatCho(
                    soLuongCan,
                    tonKhaDung
            );
        }
    }

    private BigDecimal getSoLuongDangGiu(
            String loaiKho,
            Integer idDoiTuong,
            LocalDateTime now
    ) {

        BigDecimal tongDangGiu =
                datChoKhoRepository
                        .tongSoLuongDangGiu(
                                loaiKho,
                                idDoiTuong,
                                now
                        );

        return tongDangGiu != null
                ? tongDangGiu
                : BigDecimal.ZERO;
    }

    private BigDecimal tinhTonKhaDung(
            BigDecimal tonVatLy,
            BigDecimal dangGiu
    ) {

        BigDecimal ton =
                tonVatLy.subtract(dangGiu);

        if (ton.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return ton;
    }

    private void kiemTraTonSauDatCho(
            BigDecimal soLuongCan,
            BigDecimal tonKhaDung
    ) {

        if (tonKhaDung.compareTo(soLuongCan) < 0) {
            throw new RuntimeException(
                    "Tồn kho vừa thay đổi do có đơn hàng khác đang giữ hàng. "
                            + "Vui lòng cập nhật giỏ hàng và thử lại."
            );
        }
    }
}