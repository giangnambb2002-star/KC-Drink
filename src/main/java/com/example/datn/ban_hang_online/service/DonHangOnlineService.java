package com.example.datn.ban_hang_online.service;
import com.example.datn.ban_hang_online.dto.checkout.ChiTietGioHangResponse;
import com.example.datn.ban_hang_online.dto.checkout.TaoDonHangOnlineRequest;
import com.example.datn.ban_hang_online.dto.checkout.TaoDonHangOnlineResponse;
import com.example.datn.ban_hang_online.dto.checkout.ToppingGioHangResponse;
import com.example.datn.ban_hang_online.dto.checkout.XemTruocGioHangRequest;
import com.example.datn.ban_hang_online.dto.checkout.XemTruocGioHangResponse;
import com.example.datn.dia_chi.entity.DiaChiKhachHang;
import com.example.datn.hoa_don.entity.HdctTopping;
import com.example.datn.hoa_don.entity.HoaDon;
import com.example.datn.hoa_don.entity.HoaDonChiTiet;
import com.example.datn.hoa_don.repository.HdctToppingRepository;
import com.example.datn.hoa_don.repository.HoaDonChiTietRepository;
import com.example.datn.hoa_don.repository.HoaDonRepository;
import com.example.datn.khach_hang.entity.KhachHang;
import com.example.datn.khuyen_mai.repository.KhuyenMaiRepository;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import com.example.datn.ban_hang_online.dto.don_hang.ChiTietDonHangOnlineResponse;
import com.example.datn.ban_hang_online.dto.don_hang.DonHangCuaToiResponse;
import com.example.datn.common.PageResponse;
import com.example.datn.hoa_don.dto.HoaDonResponse;
import com.example.datn.hoa_don.service.HoaDonService;
import com.example.datn.payos.dto.PayOSPaymentStatusResponse;
import com.example.datn.payos.service.PayOSService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.example.datn.van_chuyen.entity.VanDonGhn;
import com.example.datn.van_chuyen.repository.VanDonGhnRepository;
import com.example.datn.voucher.entity.Voucher;
import com.example.datn.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
@Service
@RequiredArgsConstructor
public class DonHangOnlineService {
    private final GioHangOnlineService gioHangOnlineService;
    private final KhachHangOnlineService khachHangOnlineService;
    private final DiaChiOnlineService diaChiOnlineService;
    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final HdctToppingRepository hdctToppingRepository;
    private final VoucherRepository voucherRepository;
    private final KhuyenMaiRepository khuyenMaiRepository;
    private final VanDonGhnRepository vanDonGhnRepository;
    private final HoaDonService hoaDonService;
    private final PayOSService payOSService;

    private final DatChoKhoService datChoKhoService;

    @Transactional
    public TaoDonHangOnlineResponse taoDonHang(
            TaoDonHangOnlineRequest request,
            TaiKhoan taiKhoan
    ) {
        validateRequest(request);
        String hinhThucThanhToan =
                chuanHoaHinhThucThanhToan(
                        request.getHinhThucThanhToan()
                );
        KhachHang khachHang =
                khachHangOnlineService
                        .getCurrentCustomer(taiKhoan);
        String clientRequestId =
                request.getClientRequestId().trim();
        /*
         * IDPOTENCY - lần gọi lại thông thường.
         *
         * Nếu FE gửi lại đúng clientRequestId
         * thì không tạo hóa đơn mới.
         */
        HoaDon hoaDonDaTonTai =
                hoaDonRepository
                        .findByClientRequestId(clientRequestId)
                        .orElse(null);
        if (hoaDonDaTonTai != null) {
            kiemTraDonHangThuocKhachHang(
                    hoaDonDaTonTai,
                    khachHang
            );
            return toResponse(
                    hoaDonDaTonTai,
                    false
            );
        }
        /*
         * Dùng lại toàn bộ nghiệp vụ preview đã test:
         *
         * - sản phẩm
         * - size
         * - CTKM
         * - topping
         * - tồn kho
         * - địa chỉ
         * - voucher
         * - GHN fee
         *
         * Không tin giá từ FE.
         */
        XemTruocGioHangRequest previewRequest =
                new XemTruocGioHangRequest();
        previewRequest.setIdDiaChi(
                request.getIdDiaChi()
        );
        previewRequest.setIdVoucher(
                request.getIdVoucher()
        );
        previewRequest.setItems(
                request.getItems()
        );
        XemTruocGioHangResponse preview =
                gioHangOnlineService.preview(
                        previewRequest,
                        taiKhoan
                );
        /*
         * Lấy lại entity địa chỉ đã xác minh ownership.
         */
        DiaChiKhachHang diaChi =
                diaChiOnlineService
                        .layDiaChiHopLeCuaKhachHang(
                                taiKhoan,
                                request.getIdDiaChi()
                        );
        validateDiaChiTaoDon(diaChi);
        String diaChiDayDu =
                taoDiaChiDayDu(diaChi);
        String ghiChu =
                chuanHoaGhiChu(
                        request.getGhiChuDonHang()
                );
        /*
         * Tiền hóa đơn:
         *
         * HOA_DON.tongTien
         * = tiền hàng SAU CTKM + topping
         *
         * HOA_DON.giamGia
         * = voucher
         *
         * HOA_DON.phiVanChuyen
         * = GHN
         */
        BigDecimal tongTien =
                preview.getTamTinhSauKhuyenMai()
                        .setScale(
                                0,
                                RoundingMode.HALF_UP
                        );
        BigDecimal giamGia =
                preview.getTienGiamVoucher() == null
                        ? BigDecimal.ZERO
                        : preview.getTienGiamVoucher()
                        .setScale(
                                0,
                                RoundingMode.HALF_UP
                        );
        BigDecimal phiVanChuyen =
                preview.getPhiVanChuyen()
                        .setScale(
                                0,
                                RoundingMode.HALF_UP
                        );
        BigDecimal thanhTien =
                tongTien
                        .subtract(giamGia)
                        .add(phiVanChuyen)
                        .max(BigDecimal.ZERO)
                        .setScale(
                                0,
                                RoundingMode.HALF_UP
                        );
        /*
         * ==============================
         * 1. HOA_DON
         * ==============================
         */
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon(
                null
        );
        hoaDon.setClientRequestId(
                clientRequestId
        );
        hoaDon.setLoaiHoaDon(
                "ONLINE"
        );
        hoaDon.setNgayTao(
                LocalDateTime.now()
        );
        hoaDon.setKhachHang(
                khachHang
        );
        hoaDon.setNhanVien(null);
        hoaDon.setTongTien(
                tongTien
        );
        hoaDon.setGiamGia(
                giamGia
        );
        hoaDon.setPhiVanChuyen(
                phiVanChuyen
        );
        hoaDon.setThanhTien(
                thanhTien
        );
        /*
         * Online v1 chỉ PayOS.
         */
        hoaDon.setHinhThucThanhToan(
                hinhThucThanhToan
        );
        /*
         * Chưa tạo link PayOS.
         */
        hoaDon.setPayosOrderCode(null);
        hoaDon.setPayosPaymentLinkId(null);
        hoaDon.setPayosStatus(null);
        /*
         * Đơn mới chỉ được tạo trong hệ thống.
         * Chưa thanh toán.
         */
        hoaDon.setTrangThai(
                "CHO_THANH_TOAN"
        );
        /*
         * Snapshot ngay trên hóa đơn.
         */
        hoaDon.setDiaChiGiaoHang(
                diaChiDayDu
        );
        hoaDon.setSdtNhanHang(
                diaChi.getSdtNguoiNhan().trim()
        );
        hoaDon.setGhiChu(
                ghiChu
        );
        /*
         * Voucher chỉ GẮN vào hóa đơn.
         *
         * KHÔNG giảm soLuong ở bước này.
         */
        if (preview.getIdVoucher() != null) {
            Voucher voucher =
                    voucherRepository
                            .findById(
                                    preview.getIdVoucher()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Voucher không còn tồn tại"
                                    )
                            );
            hoaDon.setVoucher(
                    voucher
            );
        } else {
            hoaDon.setVoucher(null);
        }
        /*
         * saveAndFlush để unique clientRequestId
         * được DB kiểm tra ngay.
         */
        hoaDon =
                hoaDonRepository
                        .saveAndFlush(hoaDon);
        hoaDon.setMaHoaDon(
                "ON" + String.format("%02d", hoaDon.getIdHoaDon())
        );

        hoaDon =
                hoaDonRepository.saveAndFlush(hoaDon);
        for (ChiTietGioHangResponse item
                : preview.getItems()) {
            HoaDonChiTiet chiTiet =
                    new HoaDonChiTiet();
            chiTiet.setHoaDon(
                    hoaDon
            );
            chiTiet.setIdSanPham(
                    item.getIdSanPham()
            );
            chiTiet.setIdSize(
                    item.getIdSize()
            );
            chiTiet.setSoLuong(
                    item.getSoLuong()
            );
            /*
             * Snapshot giá gốc một đơn vị:
             *
             * giá sản phẩm gốc + phụ thu size.
             */
            BigDecimal giaGocDonVi =
                    item.getGiaSanPhamGoc()
                            .add(
                                    item.getPhuThuSize() == null
                                            ? BigDecimal.ZERO
                                            : item.getPhuThuSize()
                            );
            chiTiet.setGiaGoc(
                    giaGocDonVi
            );
            /*
             * Giá sau CTKM + size.
             */
            chiTiet.setDonGia(
                    item.getDonGiaSauKhuyenMai()
            );
            chiTiet.setTienGiamKhuyenMai(
                    item.getTienGiamKhuyenMai()
            );
            /*
             * thanhTien của HDCT KHÔNG gồm topping.
             */
            chiTiet.setThanhTien(
                    item.getTienSanPhamSauKhuyenMai()
            );
            chiTiet.setMucDuong(
                    item.getMucDuong()
            );
            chiTiet.setMucDa(
                    item.getMucDa()
            );
            chiTiet.setGhiChu(
                    item.getGhiChu()
            );
            if (item.getIdKm() != null) {
                chiTiet.setKhuyenMai(
                        khuyenMaiRepository
                                .getReferenceById(
                                        item.getIdKm()
                                )
                );
            } else {
                chiTiet.setKhuyenMai(null);
            }
            HoaDonChiTiet chiTietDaLuu =
                    hoaDonChiTietRepository
                            .save(chiTiet);
            /*
             * ==============================
             * 3. HDCT_TOPPING
             * ==============================
             *
             * Dùng giá do Backend preview trả về.
             *
             * Tuyệt đối không lấy giá từ request FE.
             */
            if (item.getToppings() != null) {
                for (ToppingGioHangResponse topping
                        : item.getToppings()) {
                    HdctTopping hdctTopping =
                            new HdctTopping();
                    hdctTopping.setHoaDonChiTiet(
                            chiTietDaLuu
                    );
                    hdctTopping.setIdTopping(
                            topping.getIdTopping()
                    );
                    /*
                     * topping x1 = 1 phần,
                     * không nhân theo số lượng ly.
                     */
                    hdctTopping.setSoLuong(
                            topping.getSoLuong()
                    );
                    hdctTopping.setDonGia(
                            topping.getDonGia()
                    );
                    hdctTopping.setThanhTien(
                            topping.getThanhTien()
                    );
                    hdctToppingRepository.save(
                            hdctTopping
                    );
                }
            }
        }
        /*
         * ==============================
         * 4. VAN_DON_GHN
         * ==============================
         *
         * Đây chỉ là SNAPSHOT giao hàng.
         *
         * Chưa gọi GHN createOrder.
         */
        /*
         * COD chưa thanh toán trước nhưng đơn đã được khách xác nhận.
         *
         * Giữ tồn để đơn khác không bán vượt số lượng
         * trong thời gian chờ nhân viên tiếp nhận.
         *
         * Chưa trừ kho vật lý tại đây.
         */
        if ("TIEN_MAT".equals(hinhThucThanhToan)) {

            datChoKhoService.datChoChoHoaDon(
                    hoaDon,
                    LocalDateTime.now().plusDays(1)
            );
        }
        VanDonGhn vanDon =
                new VanDonGhn();
        vanDon.setHoaDon(
                hoaDon
        );
        /*
         * Giữ liên kết với địa chỉ gốc
         * nhưng các field bên dưới là snapshot.
         */
        vanDon.setDiaChiKhachHang(
                diaChi
        );
        vanDon.setTenNguoiNhan(
                diaChi.getTenNguoiNhan().trim()
        );
        vanDon.setSdtNguoiNhan(
                diaChi.getSdtNguoiNhan().trim()
        );
        vanDon.setDiaChiGiaoHang(
                diaChiDayDu
        );
        vanDon.setProvinceId(
                diaChi.getProvinceId()
        );
        vanDon.setDistrictId(
                diaChi.getDistrictId()
        );
        vanDon.setWardCode(
                diaChi.getWardCode().trim()
        );
        vanDon.setTenTinhThanh(
                diaChi.getTenTinhThanh()
        );
        vanDon.setTenQuanHuyen(
                diaChi.getTenQuanHuyen()
        );
        vanDon.setTenPhuongXa(
                diaChi.getTenPhuongXa()
        );
        vanDon.setPhiVanChuyen(
                phiVanChuyen
        );
        /*
         * Online PayOS-only nên không COD.
         */
        if ("TIEN_MAT".equals(hinhThucThanhToan)) {
            vanDon.setCodAmount(
                    thanhTien.intValueExact()
            );

        } else {

            vanDon.setCodAmount(0);
        }

        vanDon.setInsuranceValue(0);
        vanDon.setMaVanDonGhn(null);
        /*
         * Chưa thanh toán nên chưa được tạo GHN.
         */
        if ("TIEN_MAT".equals(hinhThucThanhToan)) {
            /*
             * COD không cần đợi PayOS.
             * Sau khi khách đặt đơn thì cửa hàng có thể tiếp nhận.
             */
            vanDon.setTrangThai(
                    "CHO_TAO_DON"
            );

        } else {

            /*
             * Chuyển khoản vẫn phải thanh toán PayOS trước.
             */
            vanDon.setTrangThai(
                    "CHO_THANH_TOAN"
            );
        }
        vanDon.setTrangThaiGhn(null);
        vanDon.setThoiGianGiaoDuKien(null);
        vanDon.setGhiChu(
                ghiChu
        );
        vanDonGhnRepository.saveAndFlush(
                vanDon
        );
        /*
         * Không:
         * - trừ kho
         * - trừ voucher
         * - tạo PayOS
         * - tạo GHN
         */
        return toResponse(
                hoaDon,
                true
        );
    }
    /*
     * Dùng sau khi DB unique index bắt được
     * hai request concurrent cùng UUID.
     */
    @Transactional(readOnly = true)
    public TaoDonHangOnlineResponse
    timLaiDonHangTheoClientRequestId(
            String clientRequestId,
            TaiKhoan taiKhoan
    ) {
        if (clientRequestId == null
                || clientRequestId.trim().isEmpty()) {
            return null;
        }
        KhachHang khachHang =
                khachHangOnlineService
                        .getCurrentCustomer(taiKhoan);
        HoaDon hoaDon =
                hoaDonRepository
                        .findByClientRequestId(
                                clientRequestId.trim()
                        )
                        .orElse(null);
        if (hoaDon == null) {
            return null;
        }
        kiemTraDonHangThuocKhachHang(
                hoaDon,
                khachHang
        );
        return toResponse(
                hoaDon,
                false
        );
    }
    private void validateRequest(
            TaoDonHangOnlineRequest request
    ) {
        if (request == null) {
            throw new RuntimeException(
                    "Dữ liệu đặt hàng không hợp lệ"
            );
        }
        if (request.getClientRequestId() == null
                || request.getClientRequestId()
                .trim()
                .isEmpty()) {
            throw new RuntimeException(
                    "Thiếu mã yêu cầu đặt hàng"
            );
        }
        if (request.getClientRequestId()
                .trim()
                .length() > 100) {
            throw new RuntimeException(
                    "Mã yêu cầu đặt hàng quá dài"
            );
        }
        if (request.getItems() == null
                || request.getItems().isEmpty()) {
            throw new RuntimeException(
                    "Giỏ hàng không có sản phẩm"
            );
        }
        if (request.getGhiChuDonHang() != null
                && request.getGhiChuDonHang()
                .trim()
                .length() > 255) {
            throw new RuntimeException(
                    "Ghi chú đơn hàng tối đa 255 ký tự"
            );
        }
    }
    private void validateDiaChiTaoDon(
            DiaChiKhachHang diaChi
    ) {
        if (diaChi.getTenNguoiNhan() == null
                || diaChi.getTenNguoiNhan()
                .trim()
                .isEmpty()) {
            throw new RuntimeException(
                    "Địa chỉ chưa có tên người nhận"
            );
        }
        if (diaChi.getSdtNguoiNhan() == null
                || diaChi.getSdtNguoiNhan()
                .trim()
                .isEmpty()) {
            throw new RuntimeException(
                    "Địa chỉ chưa có số điện thoại người nhận"
            );
        }
        if (diaChi.getDistrictId() == null
                || diaChi.getDistrictId() <= 0
                || diaChi.getWardCode() == null
                || diaChi.getWardCode()
                .trim()
                .isEmpty()) {
            throw new RuntimeException(
                    "Địa chỉ chưa đủ mã quận/huyện và phường/xã"
            );
        }
        if (diaChi.getTenTinhThanh() == null
                || diaChi.getTenTinhThanh()
                .trim()
                .isEmpty()
                || diaChi.getTenQuanHuyen() == null
                || diaChi.getTenQuanHuyen()
                .trim()
                .isEmpty()
                || diaChi.getTenPhuongXa() == null
                || diaChi.getTenPhuongXa()
                .trim()
                .isEmpty()) {
            throw new RuntimeException(
                    "Địa chỉ chưa đủ thông tin tỉnh/quận/phường"
            );
        }
    }
    private void kiemTraDonHangThuocKhachHang(
            HoaDon hoaDon,
            KhachHang khachHang
    ) {
        if (hoaDon.getKhachHang() == null
                || !khachHang.getIdKhachHang()
                .equals(
                        hoaDon.getKhachHang()
                                .getIdKhachHang()
                )) {
            throw new RuntimeException(
                    "Mã yêu cầu đặt hàng đã được sử dụng"
            );
        }
        if (!"ONLINE".equals(
                hoaDon.getLoaiHoaDon()
        )) {
            throw new RuntimeException(
                    "Mã yêu cầu đặt hàng không hợp lệ"
            );
        }
    }
//    private String taoMaHoaDonOnline() {
//        int random =
//                ThreadLocalRandom.current()
//                        .nextInt(100, 1000);
//        /*
//         * ON + 13 số millis + 3 random
//         * = 18 ký tự, vừa cột ma_hoa_don length 20.
//         */
//        return "ON"
//                + System.currentTimeMillis()
//                + random;
//    }
    private String taoDiaChiDayDu(
            DiaChiKhachHang diaChi
    ) {
        List<String> parts =
                new ArrayList<>();
        themPhanDiaChi(
                parts,
                diaChi.getDiaChi()
        );
        themPhanDiaChi(
                parts,
                diaChi.getTenPhuongXa()
        );
        themPhanDiaChi(
                parts,
                diaChi.getTenQuanHuyen()
        );
        themPhanDiaChi(
                parts,
                diaChi.getTenTinhThanh()
        );
        if (parts.isEmpty()) {
            throw new RuntimeException(
                    "Địa chỉ giao hàng không hợp lệ"
            );
        }
        return String.join(
                ", ",
                parts
        );
    }
    private void themPhanDiaChi(
            List<String> parts,
            String value
    ) {
        if (value != null
                && !value.trim().isEmpty()) {
            parts.add(
                    value.trim()
            );
        }
    }
    private String chuanHoaGhiChu(
            String ghiChu
    ) {
        return ghiChu == null
                || ghiChu.trim().isEmpty()
                ? null
                : ghiChu.trim();
    }
    private TaoDonHangOnlineResponse toResponse(
            HoaDon hoaDon,
            boolean taoMoi
    ) {
        VanDonGhn vanDon =
                vanDonGhnRepository
                        .findByHoaDon_IdHoaDon(
                                hoaDon.getIdHoaDon()
                        )
                        .orElse(null);
        Voucher voucher =
                hoaDon.getVoucher();
        KhachHang khachHang =
                hoaDon.getKhachHang();
        return TaoDonHangOnlineResponse
                .builder()
                .taoMoi(taoMoi)
                .idHoaDon(
                        hoaDon.getIdHoaDon()
                )
                .maHoaDon(
                        hoaDon.getMaHoaDon()
                )
                .clientRequestId(
                        hoaDon.getClientRequestId()
                )
                .loaiHoaDon(
                        hoaDon.getLoaiHoaDon()
                )
                .ngayTao(
                        hoaDon.getNgayTao()
                )
                .trangThai(
                        hoaDon.getTrangThai()
                )
                .hinhThucThanhToan(
                        hoaDon.getHinhThucThanhToan()
                )
                .idKhachHang(
                        khachHang == null
                                ? null
                                : khachHang.getIdKhachHang()
                )
                .idVoucher(
                        voucher == null
                                ? null
                                : voucher.getIdVoucher()
                )
                .maVoucher(
                        voucher == null
                                ? null
                                : voucher.getMaVoucher()
                )
                .tongTien(
                        hoaDon.getTongTien()
                )
                .giamGia(
                        hoaDon.getGiamGia()
                )
                .phiVanChuyen(
                        hoaDon.getPhiVanChuyen()
                )
                .thanhTien(
                        hoaDon.getThanhTien()
                )
                .idDiaChi(
                        vanDon == null
                                || vanDon.getDiaChiKhachHang() == null
                                ? null
                                : vanDon.getDiaChiKhachHang()
                                .getIdDiaChi()
                )
                .tenNguoiNhan(
                        vanDon == null
                                ? null
                                : vanDon.getTenNguoiNhan()
                )
                .sdtNguoiNhan(
                        vanDon == null
                                ? null
                                : vanDon.getSdtNguoiNhan()
                )
                .diaChiGiaoHang(
                        vanDon == null
                                ? hoaDon.getDiaChiGiaoHang()
                                : vanDon.getDiaChiGiaoHang()
                )
                .ghiChu(
                        hoaDon.getGhiChu()
                )
                .payosOrderCode(
                        hoaDon.getPayosOrderCode()
                )
                .payosPaymentLinkId(
                        hoaDon.getPayosPaymentLinkId()
                )
                .payosStatus(
                        hoaDon.getPayosStatus()
                )
                .trangThaiVanDon(
                        vanDon == null
                                ? null
                                : vanDon.getTrangThai()
                )
                .maVanDonGhn(
                        vanDon == null
                                ? null
                                : vanDon.getMaVanDonGhn()
                )
                .trangThaiGhn(
                        vanDon == null
                                ? null
                                : vanDon.getTrangThaiGhn()
                )
                .build();
    }
    @Transactional(readOnly = true)
    public PageResponse<DonHangCuaToiResponse> getDonHangCuaToi(
            TaiKhoan taiKhoan,
            int page,
            int size
    ) {
        KhachHang khachHang =
                khachHangOnlineService
                        .getCurrentCustomer(taiKhoan);
        int safePage =
                Math.max(page, 0);
        int safeSize =
                Math.min(
                        Math.max(size, 1),
                        50
                );
        Pageable pageable =
                PageRequest.of(
                        safePage,
                        safeSize,
                        Sort.by(
                                Sort.Direction.DESC,
                                "ngayTao"
                        )
                );
        Page<HoaDon> hoaDonPage =
                hoaDonRepository
                        .findByKhachHang_IdKhachHangAndLoaiHoaDon(
                                khachHang.getIdKhachHang(),
                                "ONLINE",
                                pageable
                        );
        List<Integer> idHoaDonList =
                hoaDonPage
                        .getContent()
                        .stream()
                        .map(HoaDon::getIdHoaDon)
                        .toList();
        Map<Integer, VanDonGhn> vanDonMap;
        if (idHoaDonList.isEmpty()) {
            vanDonMap = Map.of();
        } else {
            vanDonMap =
                    vanDonGhnRepository
                            .findAllByHoaDon_IdHoaDonIn(
                                    idHoaDonList
                            )
                            .stream()
                            .collect(
                                    Collectors.toMap(
                                            vanDon ->
                                                    vanDon
                                                            .getHoaDon()
                                                            .getIdHoaDon(),
                                            Function.identity(),
                                            (oldValue, newValue) ->
                                                    oldValue
                                    )
                            );
        }
        List<DonHangCuaToiResponse> content =
                hoaDonPage
                        .getContent()
                        .stream()
                        .map(hoaDon -> {
                            VanDonGhn vanDon =
                                    vanDonMap.get(
                                            hoaDon.getIdHoaDon()
                                    );
                            return DonHangCuaToiResponse
                                    .builder()
                                    .idHoaDon(
                                            hoaDon.getIdHoaDon()
                                    )
                                    .maHoaDon(
                                            hoaDon.getMaHoaDon()
                                    )
                                    .ngayTao(
                                            hoaDon.getNgayTao()
                                    )
                                    .trangThai(
                                            hoaDon.getTrangThai()
                                    )
                                    .payosStatus(
                                            hoaDon.getPayosStatus()
                                    )
                                    .tongTien(
                                            hoaDon.getTongTien()
                                    )
                                    .giamGia(
                                            hoaDon.getGiamGia()
                                    )
                                    .phiVanChuyen(
                                            hoaDon.getPhiVanChuyen()
                                    )
                                    .thanhTien(
                                            hoaDon.getThanhTien()
                                    )
                                    .trangThaiVanDon(
                                            vanDon == null
                                                    ? null
                                                    : vanDon.getTrangThai()
                                    )
                                    .maVanDonGhn(
                                            vanDon == null
                                                    ? null
                                                    : vanDon.getMaVanDonGhn()
                                    )
                                    .trangThaiGhn(
                                            vanDon == null
                                                    ? null
                                                    : vanDon.getTrangThaiGhn()
                                    )
                                    .build();
                        })
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
    @Transactional(readOnly = true)
    public ChiTietDonHangOnlineResponse getChiTietDonHang(
            Integer idHoaDon,
            TaiKhoan taiKhoan
    ) {
        HoaDon hoaDon =
                getDonHangOnlineCuaKhachHang(
                        idHoaDon,
                        taiKhoan
                );
        return toChiTietDonHangResponse(
                hoaDon
        );
    }
    @Transactional
    public ChiTietDonHangOnlineResponse huyDonHang(
            Integer idHoaDon,
            TaiKhoan taiKhoan
    ) {
        HoaDon hoaDon =
                getDonHangOnlineForUpdate(
                        idHoaDon,
                        taiKhoan
                );
        /*
         * Gọi lại nhiều lần vẫn an toàn.
         */
        if ("DA_HUY".equals(
                hoaDon.getTrangThai()
        )) {
            return toChiTietDonHangResponse(
                    hoaDon
            );
        }
        /*
         * v1 không cho USER tự hủy đơn đã thanh toán.
         */
        if ("DA_THANH_TOAN".equals(
                hoaDon.getTrangThai()
        )) {
            throw new RuntimeException(
                    "Đơn hàng đã thanh toán, không thể tự hủy"
            );
        }
        if (!"CHO_THANH_TOAN".equals(
                hoaDon.getTrangThai()
        )) {
            throw new RuntimeException(
                    "Trạng thái đơn hàng không cho phép hủy"
            );
        }
        boolean laTienMat =
                "TIEN_MAT".equalsIgnoreCase(
                        hoaDon.getHinhThucThanhToan()
                );

        if (laTienMat) {

            VanDonGhn vanDon =
                    vanDonGhnRepository
                            .findByHoaDon_IdHoaDon(idHoaDon)
                            .orElse(null);

            /*
             * COD chỉ cho khách tự hủy
             * trước khi cửa hàng tiếp nhận.
             */
            if (vanDon != null
                    && (
                    "DA_TIEP_NHAN".equals(vanDon.getTrangThai())
                            || "DA_TAO_DON".equals(vanDon.getTrangThai())
                            || "DANG_GIAO".equals(vanDon.getTrangThai())
                            || "GIAO_THANH_CONG".equals(vanDon.getTrangThai())
                            || (vanDon.getMaVanDonGhn() != null
                            && !vanDon.getMaVanDonGhn().isBlank())
            )) {
                throw new RuntimeException(
                        "Đơn hàng đã được cửa hàng tiếp nhận, không thể tự hủy"
                );
            }
        }
        /*
         * Nếu đã từng tạo PayOS thì phải hỏi PayOS thật,
         * không chỉ tin payosStatus trong DB.
         */
        if (hoaDon.getPayosOrderCode() != null) {
            PayOSPaymentStatusResponse payment =
                    payOSService
                            .getPaymentStatusInfo(
                                    hoaDon.getPayosOrderCode()
                            );
            kiemTraSoTienPayOS(
                    hoaDon,
                    payment
            );
            String status =
                    payment.getStatus();
            /*
             * Trường hợp khách vừa thanh toán nhưng FE
             * chưa kịp poll payment-status.
             *
             * Tuyệt đối không được hủy đơn.
             */
            if ("PAID".equalsIgnoreCase(status)) {
                hoaDon.setPayosStatus(
                        "PAID"
                );
                hoaDonRepository.save(
                        hoaDon
                );
                throw new RuntimeException(
                        "Giao dịch đã được thanh toán, không thể hủy đơn. "
                                + "Vui lòng kiểm tra lại trạng thái thanh toán"
                );
            }
            if ("PENDING".equalsIgnoreCase(status)) {
                PayOSPaymentStatusResponse cancelled =
                        payOSService.cancelPayment(
                                hoaDon.getPayosOrderCode(),
                                "Khach hang huy don online"
                        );
                kiemTraSoTienPayOS(
                        hoaDon,
                        cancelled
                );
                if (!"CANCELLED".equalsIgnoreCase(
                        cancelled.getStatus()
                )) {
                    throw new RuntimeException(
                            "PayOS chưa xác nhận hủy giao dịch"
                    );
                }
                hoaDon.setPayosStatus(
                        "CANCELLED"
                );
            } else if ("CANCELLED".equalsIgnoreCase(status)
                    || "EXPIRED".equalsIgnoreCase(status)) {
                hoaDon.setPayosStatus(
                        status
                );
            } else {
                throw new RuntimeException(
                        "Trạng thái PayOS hiện tại không cho phép hủy đơn"
                );
            }
        }
        datChoKhoService.giaiPhongDatCho(
                idHoaDon
        );
        hoaDon.setTrangThai(
                "DA_HUY"
        );
        hoaDonRepository.save(
                hoaDon
        );
        vanDonGhnRepository
                .findByHoaDon_IdHoaDon(
                        idHoaDon
                )
                .ifPresent(vanDon -> {
                    vanDon.setTrangThai(
                            "DA_HUY"
                    );
                    vanDonGhnRepository.save(
                            vanDon
                    );
                });
        return toChiTietDonHangResponse(
                hoaDon
        );
    }
    private HoaDon getDonHangOnlineForUpdate(
            Integer idHoaDon,
            TaiKhoan taiKhoan
    ) {
        KhachHang khachHang =
                khachHangOnlineService
                        .getCurrentCustomer(taiKhoan);
        HoaDon hoaDon =
                hoaDonRepository
                        .findByIdForUpdate(idHoaDon)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy đơn hàng"
                                )
                        );
        if (!"ONLINE".equals(
                hoaDon.getLoaiHoaDon()
        )) {
            throw new RuntimeException(
                    "Đơn hàng không phải đơn online"
            );
        }
        if (hoaDon.getKhachHang() == null
                || !khachHang.getIdKhachHang()
                .equals(
                        hoaDon.getKhachHang()
                                .getIdKhachHang()
                )) {
            throw new RuntimeException(
                    "Đơn hàng không thuộc khách hàng đang đăng nhập"
            );
        }
        return hoaDon;
    }
    private HoaDon getDonHangOnlineCuaKhachHang(
            Integer idHoaDon,
            TaiKhoan taiKhoan
    ) {
        KhachHang khachHang =
                khachHangOnlineService
                        .getCurrentCustomer(
                                taiKhoan
                        );
        HoaDon hoaDon =
                hoaDonRepository
                        .findById(idHoaDon)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy đơn hàng"
                                )
                        );
        if (!"ONLINE".equals(
                hoaDon.getLoaiHoaDon()
        )) {
            throw new RuntimeException(
                    "Đơn hàng không phải đơn online"
            );
        }
        if (hoaDon.getKhachHang() == null
                || !khachHang
                .getIdKhachHang()
                .equals(
                        hoaDon.getKhachHang()
                                .getIdKhachHang()
                )) {
            throw new RuntimeException(
                    "Đơn hàng không thuộc khách hàng đang đăng nhập"
            );
        }
        return hoaDon;
    }
    private void kiemTraSoTienPayOS(
            HoaDon hoaDon,
            PayOSPaymentStatusResponse payment
    ) {
        if (payment == null
                || payment.getAmount() == null) {
            throw new RuntimeException(
                    "PayOS không trả về số tiền giao dịch"
            );
        }
        Long expectedAmount =
                hoaDon.getThanhTien()
                        .longValueExact();
        if (!expectedAmount.equals(
                payment.getAmount()
        )) {
            throw new RuntimeException(
                    "Số tiền giao dịch PayOS không khớp với đơn hàng"
            );
        }
    }
    private ChiTietDonHangOnlineResponse
    toChiTietDonHangResponse(
            HoaDon hoaDon
    ) {
        HoaDonResponse hoaDonResponse =
                hoaDonService.getById(
                        hoaDon.getIdHoaDon()
                );
        VanDonGhn vanDon =
                vanDonGhnRepository
                        .findByHoaDon_IdHoaDon(
                                hoaDon.getIdHoaDon()
                        )
                        .orElse(null);
        return ChiTietDonHangOnlineResponse
                .builder()
                .idHoaDon(
                        hoaDonResponse.getIdHoaDon()
                )
                .maHoaDon(
                        hoaDonResponse.getMaHoaDon()
                )
                .ngayTao(
                        hoaDonResponse.getNgayTao()
                )
                .trangThai(
                        hoaDonResponse.getTrangThai()
                )
                .hinhThucThanhToan(
                        hoaDonResponse.getHinhThucThanhToan()
                )
                .payosStatus(
                        hoaDonResponse.getPayosStatus()
                )
                .payosOrderCode(
                        hoaDonResponse.getPayosOrderCode()
                )
                .payosExpiresAt(
                        hoaDon.getPayosExpiresAt()
                )
                .tongTien(
                        hoaDonResponse.getTongTien()
                )
                .giamGia(
                        hoaDonResponse.getGiamGia()
                )
                .giamGiaKhuyenMai(
                        hoaDonResponse.getGiamGiaKhuyenMai()
                )
                .phiVanChuyen(
                        hoaDonResponse.getPhiVanChuyen()
                )
                .thanhTien(
                        hoaDonResponse.getThanhTien()
                )
                .idVoucher(
                        hoaDonResponse.getIdVoucher()
                )
                .maVoucher(
                        hoaDonResponse.getMaVoucher()
                )
                .tenVoucher(
                        hoaDonResponse.getTenVoucher()
                )
                .ghiChu(
                        hoaDonResponse.getGhiChu()
                )
                .idDiaChi(
                        vanDon == null
                                || vanDon.getDiaChiKhachHang() == null
                                ? null
                                : vanDon
                                .getDiaChiKhachHang()
                                .getIdDiaChi()
                )
                .tenNguoiNhan(
                        vanDon == null
                                ? null
                                : vanDon.getTenNguoiNhan()
                )
                .sdtNguoiNhan(
                        vanDon == null
                                ? null
                                : vanDon.getSdtNguoiNhan()
                )
                .diaChiGiaoHang(
                        vanDon == null
                                ? hoaDon.getDiaChiGiaoHang()
                                : vanDon.getDiaChiGiaoHang()
                )
                .trangThaiVanDon(
                        vanDon == null
                                ? null
                                : vanDon.getTrangThai()
                )
                .maVanDonGhn(
                        vanDon == null
                                ? null
                                : vanDon.getMaVanDonGhn()
                )
                .trangThaiGhn(
                        vanDon == null
                                ? null
                                : vanDon.getTrangThaiGhn()
                )
                .thoiGianGiaoDuKien(
                        vanDon == null
                                ? null
                                : vanDon.getThoiGianGiaoDuKien()
                )
                .chiTiet(
                        hoaDonResponse.getChiTiet()
                )
                .build();
    }
    private String chuanHoaHinhThucThanhToan(
            String hinhThucThanhToan
    ) {

        if (hinhThucThanhToan == null
                || hinhThucThanhToan.trim().isEmpty()) {

            /*
             * Giữ tương thích với FE cũ.
             * Nếu FE chưa gửi field này thì mặc định PayOS.
             */
            return "CHUYEN_KHOAN";
        }

        String value =
                hinhThucThanhToan
                        .trim()
                        .toUpperCase();

        if (!"CHUYEN_KHOAN".equals(value)
                && !"TIEN_MAT".equals(value)) {

            throw new RuntimeException(
                    "Hình thức thanh toán không hợp lệ"
            );
        }

        return value;
    }
}