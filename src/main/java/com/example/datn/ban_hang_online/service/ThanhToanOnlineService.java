package com.example.datn.ban_hang_online.service;
import com.example.datn.ban_hang_online.dto.checkout.TrangThaiThanhToanOnlineResponse;
import com.example.datn.hoa_don.dto.ThanhToanHoaDonRequest;
import com.example.datn.hoa_don.entity.HoaDon;
import com.example.datn.hoa_don.repository.HoaDonRepository;
import com.example.datn.hoa_don.service.HoaDonService;
import com.example.datn.khach_hang.entity.KhachHang;
import com.example.datn.payos.dto.PayOSCreateResponse;
import com.example.datn.payos.dto.PayOSPaymentStatusResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import com.example.datn.payos.service.PayOSService;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import com.example.datn.van_chuyen.entity.VanDonGhn;
import com.example.datn.van_chuyen.repository.VanDonGhnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class ThanhToanOnlineService {
    private final HoaDonRepository hoaDonRepository;
    private final KhachHangOnlineService khachHangOnlineService;
    private final HoaDonService hoaDonService;
    private final PayOSService payOSService;
    private final VanDonGhnRepository vanDonGhnRepository;
    private final DatChoKhoService datChoKhoService;
    /*
     * ==========================================
     * TẠO LINK / QR PAYOS
     * ==========================================
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public PayOSCreateResponse taoThanhToan(
            Integer idHoaDon,
            TaiKhoan taiKhoan
    ) {
        HoaDon hoaDon =
                getDonHangOnlineForUpdate(
                        idHoaDon,
                        taiKhoan
                );
        if (!"CHUYEN_KHOAN".equalsIgnoreCase(
                hoaDon.getHinhThucThanhToan()
        )) {
            throw new RuntimeException(
                    "Đơn hàng thanh toán khi nhận hàng không sử dụng PayOS"
            );
        }

        if (!"CHO_THANH_TOAN".equals(
                hoaDon.getTrangThai()
        )) {
            throw new RuntimeException(
                    "Đơn hàng không còn ở trạng thái chờ thanh toán"
            );
        }
        if (hoaDon.getThanhTien() == null
                || hoaDon.getThanhTien()
                .signum() <= 0) {
            throw new RuntimeException(
                    "Số tiền thanh toán không hợp lệ"
            );
        }
        if (hoaDon.getPayosOrderCode() != null) {
            PayOSPaymentStatusResponse current =
                    payOSService
                            .getPaymentStatusInfo(
                                    hoaDon.getPayosOrderCode()
                            );
            kiemTraSoTienPayOS(
                    hoaDon,
                    current
            );
            String status =
                    current.getStatus();
            hoaDon.setPayosStatus(
                    status
            );
            hoaDonRepository.save(
                    hoaDon
            );
            /*
             * ==========================================
             * PENDING
             *
             * KHÔNG tạo PayOS mới.
             * KHÔNG reserve lại.
             *
             * Trả lại QR / checkoutUrl đã lưu.
             * ==========================================
             */
            if ("PENDING".equalsIgnoreCase(status)) {
                if (hoaDon.getPayosExpiresAt() != null
                        && !LocalDateTime.now()
                        .isBefore(hoaDon.getPayosExpiresAt())) {
                    throw new RuntimeException(
                            "Đơn hàng đã hết thời gian thanh toán. "
                                    + "Vui lòng tạo đơn hàng mới."
                    );
                }
                if (hoaDon.getPayosCheckoutUrl() == null
                        || hoaDon.getPayosCheckoutUrl()
                        .trim()
                        .isEmpty()
                        || hoaDon.getPayosQrCode() == null
                        || hoaDon.getPayosQrCode()
                        .trim()
                        .isEmpty()) {
                    throw new RuntimeException(
                            "Giao dịch PayOS đang chờ thanh toán nhưng dữ liệu QR cũ không còn."
                    );
                }
                return taoPayOSResponseTuHoaDon(
                        hoaDon,
                        "PENDING"
                );
            }

            if ("PAID".equalsIgnoreCase(status)) {

                ThanhToanHoaDonRequest request =
                        new ThanhToanHoaDonRequest();

                request.setHinhThucThanhToan(
                        "CHUYEN_KHOAN"
                );

                hoaDonService.thanhToanHoaDon(
                        idHoaDon,
                        request
                );

                datChoKhoService.tieuThuDatCho(
                        idHoaDon
                );

                HoaDon paid =
                        hoaDonRepository
                                .findByIdForUpdate(idHoaDon)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Không tìm thấy đơn hàng"
                                        )
                                );

                return taoPayOSResponseTuHoaDon(
                        paid,
                        "PAID"
                );
            }
            if ("CANCELLED".equalsIgnoreCase(status)
                    || "EXPIRED".equalsIgnoreCase(status)) {

                throw new RuntimeException(
                        "Đơn hàng đã hết thời gian thanh toán hoặc giao dịch đã bị hủy. "
                                + "Vui lòng tạo đơn hàng mới."
                );
            }
            throw new RuntimeException(
                    "Trạng thái PayOS hiện tại không cho phép tạo giao dịch mới"
            );
        }
        /*
         * ==========================================
         * TẠO PAYMENT MỚI
         * ==========================================
         */
        /*
         * Check tồn + voucher lần cuối.
         *
         * Chưa trừ kho thật.
         */
        hoaDonService
                .kiemTraDieuKienThanhToan(
                        idHoaDon
                );
        /*
         * Giữ kho cho payment mới.
         */
        Long amount =
                hoaDon.getThanhTien()
                        .longValueExact();
        String description =
                "KC " + hoaDon.getMaHoaDon();
        Instant expirationInstant =
                Instant.now().plusSeconds(
                        PayOSService.QR_EXPIRATION_SECONDS
                );
        LocalDateTime payosExpiresAt =
                LocalDateTime.ofInstant(
                        expirationInstant,
                        ZoneId.systemDefault()
                );
        hoaDon.setPayosExpiresAt(
                payosExpiresAt
        );
        datChoKhoService.datChoChoHoaDon(
                hoaDon,
                payosExpiresAt
        );
        PayOSCreateResponse payment =
                payOSService.createPayment(
                        amount,
                        description,
                        expirationInstant.getEpochSecond()
                );
        if (!amount.equals(
                payment.getAmount()
        )) {
            throw new RuntimeException(
                    "Số tiền PayOS không khớp với đơn hàng"
            );
        }
        /*
         * Snapshot toàn bộ thông tin cần để
         * có thể mở lại đúng QR sau này.
         */
        hoaDon.setPayosOrderCode(
                payment.getOrderCode()
        );
        hoaDon.setPayosPaymentLinkId(
                payment.getPaymentLinkId()
        );
        hoaDon.setPayosCheckoutUrl(
                payment.getCheckoutUrl()
        );
        hoaDon.setPayosQrCode(
                payment.getQrCode()
        );
        hoaDon.setPayosStatus(
                payment.getStatus()
        );
        hoaDonRepository.save(
                hoaDon
        );
        return payment;
    }
    /*
     * ==========================================
     * KIỂM TRA TRẠNG THÁI
     * ==========================================
     *
     * Nếu PayOS = PAID:
     *
     * reuse HoaDonService.thanhToanHoaDon()
     * để:
     *
     * - check kho lần cuối
     * - check voucher lần cuối
     * - trừ kho
     * - trừ voucher
     * - DA_THANH_TOAN
     * - VAN_DON_GHN -> CHO_TAO_DON
     */
    @Transactional
    public TrangThaiThanhToanOnlineResponse
    kiemTraTrangThaiThanhToan(
            Integer idHoaDon,
            TaiKhoan taiKhoan
    ) {
        HoaDon hoaDon =
                getDonHangOnlineForUpdate(
                        idHoaDon,
                        taiKhoan
                );
        if (!"CHUYEN_KHOAN".equalsIgnoreCase(
                hoaDon.getHinhThucThanhToan()
        )) {
            throw new RuntimeException(
                    "Đơn hàng thanh toán khi nhận hàng không sử dụng PayOS"
            );
        }
        if ("DA_THANH_TOAN".equals(
                hoaDon.getTrangThai()
        )) {
            datChoKhoService.tieuThuDatCho(
                    idHoaDon
            );
            return toResponse(
                    hoaDon,
                    true
            );
        }
        if (!"CHO_THANH_TOAN".equals(
                hoaDon.getTrangThai()
        )) {
            throw new RuntimeException(
                    "Trạng thái đơn hàng không cho phép kiểm tra thanh toán"
            );
        }
        if (hoaDon.getPayosOrderCode() == null) {
            throw new RuntimeException(
                    "Đơn hàng chưa có giao dịch PayOS"
            );
        }
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
        hoaDon.setPayosStatus(
                status
        );
        hoaDonRepository.save(
                hoaDon
        );
        /*
         * Chưa trả tiền.
         */
        if (!"PAID".equalsIgnoreCase(status)) {
            if ("CANCELLED".equalsIgnoreCase(status)
                    || "EXPIRED".equalsIgnoreCase(status)) {
                huyDonQuaHanDaLock(
                        hoaDon,
                        status
                );
            }
            return toResponse(
                    hoaDon,
                    false
            );
        }
        ThanhToanHoaDonRequest request =
                new ThanhToanHoaDonRequest();
        request.setHinhThucThanhToan(
                "CHUYEN_KHOAN"
        );
        hoaDonService.thanhToanHoaDon(
                idHoaDon,
                request
        );
        datChoKhoService.tieuThuDatCho(
                idHoaDon
        );
/*
 * Lấy lại entity sau khi
        /*
         * Lấy lại entity sau khi
         * hoàn tất thanh toán.
         */
        HoaDon hoaDonDaThanhToan =
                hoaDonRepository
                        .findByIdForUpdate(
                                idHoaDon
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy đơn hàng"
                                )
                        );
        return toResponse(
                hoaDonDaThanhToan,
                true
        );
    }
    /*
     * ==========================================
     * HỦY QR / PAYMENT PENDING
     * ==========================================
     *
     * Dùng khi:
     * - khách thoát thanh toán
     * - muốn sinh QR mới
     */
    @Transactional
    public TrangThaiThanhToanOnlineResponse
    huyThanhToan(
            Integer idHoaDon,
            TaiKhoan taiKhoan
    ) {
        HoaDon hoaDon =
                getDonHangOnlineForUpdate(
                        idHoaDon,
                        taiKhoan
                );
        if (!"CHUYEN_KHOAN".equalsIgnoreCase(
                hoaDon.getHinhThucThanhToan()
        )) {
            throw new RuntimeException(
                    "Đơn hàng thanh toán khi nhận hàng không sử dụng PayOS"
            );
        }
        if (!"CHO_THANH_TOAN".equals(
                hoaDon.getTrangThai()
        )) {
            throw new RuntimeException(
                    "Đơn hàng không còn chờ thanh toán"
            );
        }
        if (hoaDon.getPayosOrderCode() == null) {
            throw new RuntimeException(
                    "Đơn hàng chưa có giao dịch PayOS"
            );
        }
        PayOSPaymentStatusResponse current =
                payOSService
                        .getPaymentStatusInfo(
                                hoaDon.getPayosOrderCode()
                        );
        kiemTraSoTienPayOS(
                hoaDon,
                current
        );
        String status =
                current.getStatus();
        if ("PAID".equalsIgnoreCase(status)) {
            hoaDon.setPayosStatus(
                    status
            );
            hoaDonRepository.save(
                    hoaDon
            );
            ThanhToanHoaDonRequest request =
                    new ThanhToanHoaDonRequest();
            request.setHinhThucThanhToan(
                    "CHUYEN_KHOAN"
            );
            hoaDonService.thanhToanHoaDon(
                    idHoaDon,
                    request
            );
            datChoKhoService.tieuThuDatCho(
                    idHoaDon
            );
            HoaDon paid =
                    hoaDonRepository
                            .findByIdForUpdate(
                                    idHoaDon
                            )
                            .orElseThrow();
            return toResponse(
                    paid,
                    true
            );
        }
        if ("CANCELLED".equalsIgnoreCase(status)
                || "EXPIRED".equalsIgnoreCase(status)) {

            huyDonQuaHanDaLock(
                    hoaDon,
                    status
            );

            return toResponse(
                    hoaDon,
                    false
            );
        }
        if (!"PENDING".equalsIgnoreCase(status)) {
            throw new RuntimeException(
                    "Trạng thái PayOS không cho phép hủy"
            );
        }
        PayOSPaymentStatusResponse cancelled =
                payOSService.cancelPayment(
                        hoaDon.getPayosOrderCode(),
                        "Khach hang huy thanh toan online"
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
        huyDonQuaHanDaLock(
                hoaDon,
                cancelled.getStatus()
        );

        return toResponse(
                hoaDon,
                false
        );
    }

    private HoaDon getDonHangOnlineForUpdate(
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
                        .findByIdForUpdate(
                                idHoaDon
                        )
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
    private PayOSCreateResponse taoPayOSResponseTuHoaDon(
            HoaDon hoaDon,
            String status
    ) {
        Long amount =
                hoaDon.getThanhTien() == null
                        ? null
                        : hoaDon.getThanhTien()
                        .longValueExact();
        String description =
                "KC " + hoaDon.getMaHoaDon();
        return new PayOSCreateResponse(
                hoaDon.getPayosOrderCode(),
                amount,
                description,
                hoaDon.getPayosCheckoutUrl(),
                hoaDon.getPayosQrCode(),
                hoaDon.getPayosPaymentLinkId(),
                status
        );
    }
    private void huyDonQuaHanDaLock(
            HoaDon hoaDon,
            String payosStatus
    ) {
        Integer idHoaDon =
                hoaDon.getIdHoaDon();
        if (payosStatus != null) {
            hoaDon.setPayosStatus(
                    payosStatus
            );
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
    }
    @Transactional
    public void xuLyDonQuaHan(
            Integer idHoaDon
    ) {
        HoaDon hoaDon =
                hoaDonRepository
                        .findByIdForUpdate(
                                idHoaDon
                        )
                        .orElse(null);
        if (hoaDon == null) {
            return;
        }
        if (!"ONLINE".equals(
                hoaDon.getLoaiHoaDon()
        )) {
            return;
        }
        if (!"CHUYEN_KHOAN".equalsIgnoreCase(
                hoaDon.getHinhThucThanhToan()
        )) {
            return;
        }
        if (!"CHO_THANH_TOAN".equals(
                hoaDon.getTrangThai()
        )) {
            return;
        }
        if (hoaDon.getPayosExpiresAt() == null
                || LocalDateTime.now()
                .isBefore(
                        hoaDon.getPayosExpiresAt()
                )) {
            return;
        }
        /*
         * Không có PayOS thì không thể thanh toán nữa.
         */
        if (hoaDon.getPayosOrderCode() == null) {
            huyDonQuaHanDaLock(
                    hoaDon,
                    hoaDon.getPayosStatus()
            );
            return;
        }
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
         * Khách đã trả tiền sát deadline.
         * Tuyệt đối không hủy.
         */
        if ("PAID".equalsIgnoreCase(status)) {
            hoaDon.setPayosStatus(
                    "PAID"
            );
            hoaDonRepository.save(
                    hoaDon
            );
            ThanhToanHoaDonRequest request =
                    new ThanhToanHoaDonRequest();
            request.setHinhThucThanhToan(
                    "CHUYEN_KHOAN"
            );
            hoaDonService.thanhToanHoaDon(
                    idHoaDon,
                    request
            );
            datChoKhoService.tieuThuDatCho(
                    idHoaDon
            );
            return;
        }
        /*
         * Đã tới deadline nhưng PayOS vẫn PENDING
         * thì chủ động đóng giao dịch.
         */
        if ("PENDING".equalsIgnoreCase(status)) {
            PayOSPaymentStatusResponse cancelled =
                    payOSService.cancelPayment(
                            hoaDon.getPayosOrderCode(),
                            "Het thoi gian thanh toan online"
                    );
            kiemTraSoTienPayOS(
                    hoaDon,
                    cancelled
            );
            if (!"CANCELLED".equalsIgnoreCase(
                    cancelled.getStatus()
            )
                    && !"EXPIRED".equalsIgnoreCase(
                    cancelled.getStatus()
            )) {

                throw new RuntimeException(
                        "PayOS chưa xác nhận đóng giao dịch quá hạn"
                );
            }
            status =
                    cancelled.getStatus();
        }
        if ("CANCELLED".equalsIgnoreCase(status)
                || "EXPIRED".equalsIgnoreCase(status)) {
            huyDonQuaHanDaLock(
                    hoaDon,
                    status
            );
            return;
        }
        throw new RuntimeException(
                "Trạng thái PayOS không hợp lệ khi xử lý đơn quá hạn: "
                        + status
        );
    }
    private TrangThaiThanhToanOnlineResponse
    toResponse(
            HoaDon hoaDon,
            boolean daThanhToan
    ) {
        VanDonGhn vanDon =
                vanDonGhnRepository
                        .findByHoaDon_IdHoaDon(
                                hoaDon.getIdHoaDon()
                        )
                        .orElse(null);
        Long amount =
                hoaDon.getThanhTien() == null
                        ? null
                        : hoaDon.getThanhTien()
                        .longValueExact();
        return TrangThaiThanhToanOnlineResponse
                .builder()
                .idHoaDon(
                        hoaDon.getIdHoaDon()
                )
                .maHoaDon(
                        hoaDon.getMaHoaDon()
                )
                .trangThaiDonHang(
                        hoaDon.getTrangThai()
                )
                .payosOrderCode(
                        hoaDon.getPayosOrderCode()
                )
                .amount(
                        amount
                )
                .payosStatus(
                        hoaDon.getPayosStatus()
                )
                .daThanhToan(
                        daThanhToan
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
                .payosExpiresAt(
                        hoaDon.getPayosExpiresAt()
                )
                .build();
    }
}