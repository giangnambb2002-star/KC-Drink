package com.example.datn.ban_hang_online.service;

import com.example.datn.ban_hang_online.entity.DatChoKho;
import com.example.datn.ban_hang_online.repository.DatChoKhoRepository;
import com.example.datn.ban_thanh_pham.entity.CongThucSanPhamBtp;
import com.example.datn.ban_thanh_pham.repository.CongThucSanPhamBtpRepository;
import com.example.datn.ban_thanh_pham.service.BanThanhPhamKhoService;
import com.example.datn.hoa_don.entity.HdctTopping;
import com.example.datn.hoa_don.entity.HoaDon;
import com.example.datn.hoa_don.entity.HoaDonChiTiet;
import com.example.datn.hoa_don.repository.HdctToppingRepository;
import com.example.datn.hoa_don.repository.HoaDonChiTietRepository;
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
public class DatChoKhoService {

//    private static final long SO_PHUT_GIU_KHO = 11L;

    private final DatChoKhoRepository datChoKhoRepository;

    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final HdctToppingRepository hdctToppingRepository;

    private final CongThucSanPhamBtpRepository
            congThucSanPhamBtpRepository;

    private final CongThucSanPhamRepository
            congThucSanPhamRepository;

    private final BanThanhPhamKhoService
            banThanhPhamKhoService;

    private final KhoService khoService;

    private final ToppingKhoService toppingKhoService;

    /*
     * ==========================================
     * GIỮ KHO CHO MỘT HÓA ĐƠN ONLINE
     * ==========================================
     */
    /*
     * Method cũ giữ lại để không ảnh hưởng
     * những nơi khác đang gọi.
     */
    public void datChoChoHoaDon(
            HoaDon hoaDon
    ) {
        datChoChoHoaDon(
                hoaDon,
                LocalDateTime.now()
                        .plusMinutes(10)
        );
    }

    /*
     * Online PayOS sẽ gọi method này
     * và truyền đúng deadline của QR vào.
     */
    public void datChoChoHoaDon(
            HoaDon hoaDon,
            LocalDateTime payosExpiresAt
    ) {

        if (hoaDon == null
                || hoaDon.getIdHoaDon() == null) {

            throw new RuntimeException(
                    "Hóa đơn không hợp lệ để giữ kho"
            );
        }

        Integer idHoaDon =
                hoaDon.getIdHoaDon();

        LocalDateTime now =
                LocalDateTime.now();

        if (payosExpiresAt == null
                || !payosExpiresAt.isAfter(now)) {

            throw new RuntimeException(
                    "Thời gian giữ kho không hợp lệ"
            );
        }
        LocalDateTime hetHanLuc =
                payosExpiresAt.plusMinutes(1);
        giaiPhongDatCho(
                idHoaDon
        );

        Map<Integer, BigDecimal> tongBtpCanDung =
                new HashMap<>();

        Map<Integer, BigDecimal> tongNguyenLieuCanDung =
                new HashMap<>();

        Map<Integer, BigDecimal> tongToppingCanDung =
                new HashMap<>();

        List<HoaDonChiTiet> chiTietList =
                hoaDonChiTietRepository
                        .findByHoaDon_IdHoaDon(
                                idHoaDon
                        );

        if (chiTietList.isEmpty()) {
            throw new RuntimeException(
                    "Hóa đơn chưa có sản phẩm"
            );
        }

        /*
         * ==========================================
         * GOM NHU CẦU TOÀN HÓA ĐƠN
         * ==========================================
         */
        for (HoaDonChiTiet chiTiet
                : chiTietList) {

            /*
             * =========================
             * BÁN THÀNH PHẨM
             * =========================
             */
            List<CongThucSanPhamBtp>
                    congThucBtpList =
                    congThucSanPhamBtpRepository
                            .findByIdSanPhamAndIdSize(
                                    chiTiet.getIdSanPham(),
                                    chiTiet.getIdSize()
                            );

            for (CongThucSanPhamBtp congThuc
                    : congThucBtpList) {

                Integer idBtp =
                        congThuc
                                .getBanThanhPham()
                                .getIdBanThanhPham();

                BigDecimal soLuongCan =
                        congThuc
                                .getSoLuongCanDung()
                                .multiply(
                                        BigDecimal.valueOf(
                                                chiTiet.getSoLuong()
                                        )
                                );

                tongBtpCanDung.merge(
                        idBtp,
                        soLuongCan,
                        BigDecimal::add
                );
            }

            /*
             * =========================
             * NGUYÊN LIỆU TRỰC TIẾP
             * =========================
             */
            List<CongThucSanPham>
                    congThucNguyenLieuList =
                    congThucSanPhamRepository
                            .findByIdSanPhamAndIdSize(
                                    chiTiet.getIdSanPham(),
                                    chiTiet.getIdSize()
                            );

            for (CongThucSanPham congThuc
                    : congThucNguyenLieuList) {

                Integer idNguyenLieu =
                        congThuc
                                .getNguyenLieu()
                                .getIdNguyenLieu();

                BigDecimal soLuongCan =
                        BigDecimal.valueOf(
                                congThuc.getSoLuongCanDung()
                                        * chiTiet.getSoLuong()
                        );

                tongNguyenLieuCanDung.merge(
                        idNguyenLieu,
                        soLuongCan,
                        BigDecimal::add
                );
            }

            /*
             * =========================
             * TOPPING
             * =========================
             */
            List<HdctTopping> toppingList =
                    hdctToppingRepository
                            .findByHoaDonChiTiet_IdHoaDonChiTiet(
                                    chiTiet
                                            .getIdHoaDonChiTiet()
                            );

            for (HdctTopping topping
                    : toppingList) {

                /*
                 * QUAN TRỌNG:
                 *
                 * HDCT_TOPPING.soLuong đã là
                 * tổng số phần topping của dòng.
                 *
                 * KHÔNG nhân tiếp số lượng ly.
                 */
                tongToppingCanDung.merge(
                        topping.getIdTopping(),
                        BigDecimal.valueOf(
                                topping.getSoLuong()
                        ),
                        BigDecimal::add
                );
            }
        }

        /*
         * ==========================================
         * CHECK TỒN KHẢ DỤNG
         * ==========================================
         */

        for (Map.Entry<Integer, BigDecimal> entry
                : tongBtpCanDung.entrySet()) {

            Integer idBtp =
                    entry.getKey();

            BigDecimal soLuongCan =
                    entry.getValue();

            /*
             * Check tồn vật lý cũ trước.
             */
            banThanhPhamKhoService
                    .kiemTraDuTon(
                            idBtp,
                            soLuongCan
                    );

            /*
             * Sau đó mới trừ phần đang được
             * các đơn ONLINE khác giữ.
             */
            kiemTraTonKhaDung(
                    "BAN_THANH_PHAM",
                    idBtp,
                    soLuongCan,
                    banThanhPhamKhoService
                            .getTongTonKhaDung(
                                    idBtp
                            ),
                    now
            );
        }

        for (Map.Entry<Integer, BigDecimal> entry
                : tongNguyenLieuCanDung.entrySet()) {

            Integer idNguyenLieu =
                    entry.getKey();

            BigDecimal soLuongCan =
                    entry.getValue();

            khoService.kiemTraDuTon(
                    idNguyenLieu,
                    soLuongCan.doubleValue()
            );

            kiemTraTonKhaDung(
                    "NGUYEN_LIEU",
                    idNguyenLieu,
                    soLuongCan,
                    khoService.getTongTonKhaDung(
                            idNguyenLieu
                    ),
                    now
            );
        }

        for (Map.Entry<Integer, BigDecimal> entry
                : tongToppingCanDung.entrySet()) {

            Integer idTopping =
                    entry.getKey();

            BigDecimal soLuongCan =
                    entry.getValue();

            int soLuongCanInt =
                    soLuongCan.intValueExact();

            toppingKhoService
                    .kiemTraDuTon(
                            idTopping,
                            soLuongCanInt
                    );

            kiemTraTonKhaDung(
                    "TOPPING",
                    idTopping,
                    soLuongCan,
                    toppingKhoService
                            .getTongTonKhaDung(
                                    idTopping
                            ),
                    now
            );
        }

        /*
         * ==========================================
         * MỌI THỨ ĐỀU ĐỦ -> TẠO RESERVATION
         * ==========================================
         */

        for (Map.Entry<Integer, BigDecimal> entry
                : tongBtpCanDung.entrySet()) {

            luuHoacKichHoatLai(
                    hoaDon,
                    "BAN_THANH_PHAM",
                    entry.getKey(),
                    entry.getValue(),
                    hetHanLuc
            );
        }

        for (Map.Entry<Integer, BigDecimal> entry
                : tongNguyenLieuCanDung.entrySet()) {

            luuHoacKichHoatLai(
                    hoaDon,
                    "NGUYEN_LIEU",
                    entry.getKey(),
                    entry.getValue(),
                    hetHanLuc
            );
        }

        for (Map.Entry<Integer, BigDecimal> entry
                : tongToppingCanDung.entrySet()) {

            luuHoacKichHoatLai(
                    hoaDon,
                    "TOPPING",
                    entry.getKey(),
                    entry.getValue(),
                    hetHanLuc
            );
        }

        /*
         * Đẩy xuống DB ngay trong transaction.
         */
        datChoKhoRepository.flush();
    }
    public void xuatKhoTuDatCho(
            Integer idHoaDon
    ) {

        List<DatChoKho> list =
                datChoKhoRepository
                        .findAllByHoaDon_IdHoaDonAndTrangThai(
                                idHoaDon,
                                "ACTIVE"
                        );

        if (list.isEmpty()) {
            throw new RuntimeException(
                    "Đơn hàng không còn dữ liệu giữ kho để tiếp nhận"
            );
        }

        /*
         * Trừ đúng lượng đã reserve khi khách đặt đơn.
         */
        for (DatChoKho datCho : list) {

            String loaiKho =
                    datCho.getLoaiKho();

            Integer idDoiTuong =
                    datCho.getIdDoiTuong();

            BigDecimal soLuong =
                    datCho.getSoLuong();

            if ("BAN_THANH_PHAM".equals(loaiKho)) {

                banThanhPhamKhoService.truBanThanhPham(
                        idDoiTuong,
                        soLuong
                );

            } else if ("NGUYEN_LIEU".equals(loaiKho)) {

                khoService.truKhoNguyenLieu(
                        idDoiTuong,
                        soLuong.doubleValue()
                );

            } else if ("TOPPING".equals(loaiKho)) {

                toppingKhoService.truKhoTopping(
                        idDoiTuong,
                        soLuong.intValueExact()
                );

            } else {

                throw new RuntimeException(
                        "Loại kho đặt chỗ không hợp lệ: "
                                + loaiKho
                );
            }
        }

        /*
         * Chỉ khi trừ kho xong toàn bộ
         * mới chuyển reservation sang CONSUMED.
         */
        tieuThuDatCho(
                idHoaDon
        );
    }
    /*
     * ==========================================
     * PAID -> CONSUMED
     * ==========================================
     */
    public void tieuThuDatCho(
            Integer idHoaDon
    ) {

        List<DatChoKho> list =
                datChoKhoRepository
                        .findAllByHoaDon_IdHoaDonAndTrangThai(
                                idHoaDon,
                                "ACTIVE"
                        );

        if (list.isEmpty()) {
            return;
        }

        LocalDateTime now =
                LocalDateTime.now();

        for (DatChoKho datCho : list) {

            datCho.setTrangThai(
                    "CONSUMED"
            );

            datCho.setNgayCapNhat(
                    now
            );
        }

        datChoKhoRepository.saveAll(
                list
        );
    }

    /*
     * ==========================================
     * CANCELLED / EXPIRED -> RELEASED
     * ==========================================
     */
    public void giaiPhongDatCho(
            Integer idHoaDon
    ) {

        List<DatChoKho> list =
                datChoKhoRepository
                        .findAllByHoaDon_IdHoaDonAndTrangThai(
                                idHoaDon,
                                "ACTIVE"
                        );

        if (list.isEmpty()) {
            return;
        }

        LocalDateTime now =
                LocalDateTime.now();

        for (DatChoKho datCho : list) {

            datCho.setTrangThai(
                    "RELEASED"
            );

            datCho.setNgayCapNhat(
                    now
            );
        }

        datChoKhoRepository
                .saveAllAndFlush(
                        list
                );
    }

    /*
     * ==========================================
     * TỒN KHẢ DỤNG
     * ==========================================
     */
    private void kiemTraTonKhaDung(
            String loaiKho,
            Integer idDoiTuong,
            BigDecimal soLuongCan,
            BigDecimal tonVatLy,
            LocalDateTime now
    ) {

        BigDecimal dangGiu =
                datChoKhoRepository
                        .tongSoLuongDangGiu(
                                loaiKho,
                                idDoiTuong,
                                now
                        );

        if (dangGiu == null) {
            dangGiu =
                    BigDecimal.ZERO;
        }

        BigDecimal tonKhaDung =
                tonVatLy.subtract(
                        dangGiu
                );

        if (tonKhaDung.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            tonKhaDung =
                    BigDecimal.ZERO;
        }

        if (tonKhaDung.compareTo(
                soLuongCan
        ) < 0) {

            throw new RuntimeException(
                    "Tồn kho vừa thay đổi do có đơn hàng khác đang giữ hàng. "
                            + "Vui lòng cập nhật giỏ hàng và thử lại."
            );
        }
    }

    /*
     * Vì DB có unique:
     *
     * id_hoa_don + loai_kho + id_doi_tuong
     *
     * nên khi tạo QR lần 2 sau EXPIRED/CANCELLED,
     * ta dùng lại dòng cũ thay vì INSERT thêm.
     */
    private void luuHoacKichHoatLai(
            HoaDon hoaDon,
            String loaiKho,
            Integer idDoiTuong,
            BigDecimal soLuong,
            LocalDateTime hetHanLuc
    ) {

        DatChoKho datCho =
                datChoKhoRepository
                        .findByHoaDon_IdHoaDonAndLoaiKhoAndIdDoiTuong(
                                hoaDon.getIdHoaDon(),
                                loaiKho,
                                idDoiTuong
                        )
                        .orElseGet(
                                DatChoKho::new
                        );

        datCho.setHoaDon(
                hoaDon
        );

        datCho.setLoaiKho(
                loaiKho
        );

        datCho.setIdDoiTuong(
                idDoiTuong
        );

        datCho.setSoLuong(
                soLuong
        );

        datCho.setTrangThai(
                "ACTIVE"
        );

        datCho.setHetHanLuc(
                hetHanLuc
        );

        datChoKhoRepository.save(
                datCho
        );
    }
}